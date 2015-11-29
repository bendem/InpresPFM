#include "server/ContainerServer.hpp"

ContainerServer::ContainerServer(unsigned short port, const string& container_file, const string& user_file, ThreadPool& pool)
        : containerFile(container_file),
          parcLocations(containerFile.load()),
          users(user_file, ';'),
          pool(pool),
          proto(),
          socket(),
          selector(),
          selectorThread(selector, pool, proto),
          closed(false), closing(false), paused(false) {
    LOG << Logger::Debug << "Loaded " << parcLocations.size() << " location from " << container_file;

    LOG << "Binding server socket to " << port;
    socket.bind(port);
}

ContainerServer::~ContainerServer() {
    this->close();
}

ContainerServer& ContainerServer::init() {
    cmmp::LoginPacket::registerHandler(std::bind(&ContainerServer::loginHandler, this, _1, _2));
    cmmp::InputTruckPacket::registerHandler(std::bind(&ContainerServer::inputTruckHandler, this, _1, _2));
    cmmp::InputDonePacket::registerHandler(std::bind(&ContainerServer::inputDoneHandler, this, _1, _2));
    cmmp::OutputReadyPacket::registerHandler(std::bind(&ContainerServer::outputReadyHandler, this, _1, _2));
    cmmp::OutputOnePacket::registerHandler(std::bind(&ContainerServer::outputOneHandler, this, _1, _2));
    cmmp::OutputDonePacket::registerHandler(std::bind(&ContainerServer::outputDoneHandler, this, _1, _2));
    cmmp::LogoutPacket::registerHandler(std::bind(&ContainerServer::logoutHandler, this, _1, _2));

    return *this;
}

ContainerServer& ContainerServer::listen() {
    while(!closed) {
        try {
            std::shared_ptr<Socket> connection = socket.accept();
            if(closing || paused) {
                LOG << Logger::Warning << "Ignoring connection from " << connection->getHost() << ", server unavailable";
                connection->close();
                continue;
            }

            LOG << Logger::Debug
                << "Connection accepted " << connection->getHandle()
                << ": " << connection->getHost() << ':' << connection->getPort();

            // Set up connection cleanup
            connection
                ->registerCloseHandler(std::bind(&ContainerServer::cleanupContainersBeingStored, this, _1, _2))
                .registerCloseHandler([this](Socket& s, Socket::CloseReason) {
                    Lock lk(this->loggedInUsersMutex);
                    if(this->loggedInUsers.erase(&s) > 0) {
                        LOG << Logger::Debug << "Removed connected user, connection closed";
                    }
                });
            this->selector.addSocket(connection);
        } catch(IOError e) {
            if(e.reset) {
                LOG << Logger::Debug << "IOError: connection reset: " << e.what();
            } else {
                LOG << Logger::Error << "IOError: " << e.what();
            }
            continue;
        }
    }

    return *this;
}

void ContainerServer::close() {
    if(this->closed.exchange(true)) {
        return;
    }
    this->socket.close();
}

std::vector<string> ContainerServer::getConnectedIps() {
    Lock lk(this->loggedInUsersMutex);

    std::vector<string> ips;
    ips.reserve(this->loggedInUsers.size());
    for(const std::pair<Socket*, string>& user : this->loggedInUsers) {
        ips.push_back(user.first->getHost() + ':' + std::to_string(user.first->getPort()));
    }

    return ips;
}

bool ContainerServer::close(unsigned int time) {
    if(closing.exchange(true)) {
        return false;
    }
    // TODO Send messages to everybody
    std::thread([time, this] {
        std::this_thread::sleep_for(std::chrono::seconds(time));
        this->close();
    });
    return true;
}

void ContainerServer::loginHandler(const cmmp::LoginPacket& p, std::shared_ptr<Socket> s) {
    string username = p.getUsername();
    if(this->isLoggedIn(s)) {
        LOG << Logger::Warning << "Ignoring connect attempt from an authentified socket: " << username;

        this->proto.write(s, cmmp::LoginResponsePacket(false, "Already logged in"));
        return;
    }

    string password = p.getPassword();

    if(p.isNew()) {
        if(username.empty() || password.empty()) {
            this->proto.write(s, cmmp::LoginResponsePacket(false, "Empty username or password"));
            return;
        }

        if(username.find_first_of("\n;\r\0") != string::npos
                || password.find_first_of("\n;\r\0") != string::npos
                || std::count_if(username.begin(), username.end(), [](char c) { return !std::isprint(c); }) != 0
                || std::count_if(password.begin(), password.end(), [](char c) { return !std::isprint(c); }) != 0) {
            this->proto.write(s, cmmp::LoginResponsePacket(false, "Invalid character in username or password"));
            return;
        }

        Lock usersLock(this->usersMutex);
        if(!this->users.find("username", username).empty()) {
            this->proto.write(s, cmmp::LoginResponsePacket(false, "Username already in use"));
            return;
        }

        // Insert and save
        this->users.insert({username, password});
        this->users.save();

        // Login
        Lock loggedInUsersLock(this->loggedInUsersMutex);
        this->loggedInUsers.insert({ s.get(), username});

        this->proto.write(s, cmmp::LoginResponsePacket(true));
        return;
    }

    Lock lk(this->usersMutex);
    std::map<string, string> map = this->users.find("username", username);
    if(map.empty()) {
        LOG << Logger::Warning << "Tried to login with unknown username: " << username;

        this->proto.write(s, cmmp::LoginResponsePacket(false, "user not found"));
        return;
    }

    if(map.begin()->second == password) {
        LOG << username << " logged in";

        Lock lk(this->loggedInUsersMutex);
        this->loggedInUsers.insert({ s.get(), username});
        this->proto.write(s, cmmp::LoginResponsePacket(true));
        return;
    }

    LOG << Logger::Warning << "Tried to login from " << username << " with invalid password";
    this->proto.write(s, cmmp::LoginResponsePacket(false, "Invalid password"));
}

void ContainerServer::inputTruckHandler(const cmmp::InputTruckPacket& p, std::shared_ptr<Socket> s) {
    if(!this->isLoggedIn(s)) {
        this->proto.write(s, cmmp::InputTruckResponsePacket(false, {}, "Not logged in"));
        return;
    }

    {
        // Check a the client isn't already storing containers
        Lock lk(this->containersBeingStoredMutex);
        if(this->containersBeingStored.find(s.get()) != this->containersBeingStored.end()) {
            this->proto.write(s, cmmp::InputTruckResponsePacket(false, {}, "Already storing containers"));
            return;
        }
    }

    std::vector<Container> containers;
    {
        Lock lk(this->parcLocationsMutex);

        Container tmp;
        for(const Container& container : p.getContainers()) {
            // Find a place for the container
            tmp = container;
            bool had_place;
            try {
                had_place = this->findFreePlace(tmp);
            } catch(std::logic_error e) {
                LOG << Logger::Error << "Something is really bad: " << e.what();
                this->proto.write(s, cmmp::InputTruckResponsePacket(false, {}, string("Something is really bad: ") + e.what()));
                return;
            }

            if(!had_place) {
                this->proto.write(s, cmmp::InputTruckResponsePacket(false, {}, "No free place available"));
                return;
            } else {
                // Save the container info for later
                containers.push_back(tmp);
            }
        }

        this->containersBeingStored.insert({ s.get(), containers });
    }

    this->proto.write(s, cmmp::InputTruckResponsePacket(true, containers));
}

void ContainerServer::inputDoneHandler(const cmmp::InputDonePacket& p, std::shared_ptr<Socket> s) {
    if(!this->isLoggedIn(s)) {
        this->proto.write(s, cmmp::InputDoneResponsePacket(false, "Not logged in"));
        return;
    }

    Lock lk(this->containersBeingStoredMutex);
    auto containersToValidate = this->containersBeingStored.find(s.get());
    if(containersToValidate == this->containersBeingStored.end()) {
        LOG << Logger::Warning << "Received InputDone even tho no containers where being stored";
        this->proto.write(s, cmmp::InputDoneResponsePacket(false, "No containers currently being stored"));
        return;
    }

    if(!p.isOk()) {
        LOG << Logger::Debug << "Something happened while storing, cleaning up";
        this->cleanupContainersBeingStored(*s, Socket::CloseReason::Error);
        this->proto.write(s, cmmp::InputDoneResponsePacket(true));
        return;
    }

    {
        Lock lk(this->parcLocationsMutex);
        LOG << Logger::Debug << containersToValidate->second.size() << " got stored";

        // Mark all places as taken
        for(Container& container : containersToValidate->second) {
            auto it = std::find_if(
                this->parcLocations.begin(),
                this->parcLocations.end(),
                [&container](const ParcLocation& location) {
                    return location.x == container.x && location.y == container.y;
                }
            );

            if(it != this->parcLocations.end()) {
                it->flag = ParkLocationFlag::Taken;
            }
        }
        this->containersBeingStored.erase(containersToValidate);

        // Save
        this->containerFile.save(this->parcLocations.begin(), this->parcLocations.end());
    }

    // TODO Do something with the weight?
    this->proto.write(s, cmmp::InputDoneResponsePacket(true));
}

void ContainerServer::outputReadyHandler(const cmmp::OutputReadyPacket& p, std::shared_ptr<Socket> s) {
    if(!this->isLoggedIn(s)) {
        this->proto.write(s, cmmp::OutputReadyResponsePacket(false, {}, "Not logged in"));
        return;
    }

    uint16_t capacity = p.getCapacity();
    if(capacity == 0 || p.getDestination().empty()) {
        this->proto.write(s, cmmp::OutputReadyResponsePacket(false, {}, "Invalid value received"));
        return;
    }

    LOG << Logger::Debug
        << "Transport '" << p.getLicense()
        << "' going to '" << p.getDestination()
        << "' can carry " << capacity << " container(s)";

    std::vector<Container> containers;
    {
        Lock lk(this->parcLocationsMutex);

        for(ParcLocation& location : this->parcLocations) {
            if(location.destination == p.getDestination()) {
                containers.emplace_back(Container {
                    location.containerId, location.destination, location.x, location.y
                });
                location.flag = ParkLocationFlag::Leaving;

                if(--capacity == 0) {
                    break;
                }
            }
        }
    }

    if(containers.empty()) {
        this->proto.write(s, cmmp::OutputReadyResponsePacket(false, {}, "No containers for this destination"));
    } else {
        this->proto.write(s, cmmp::OutputReadyResponsePacket(true, containers));
    }

}

void ContainerServer::outputOneHandler(const cmmp::OutputOnePacket& p, std::shared_ptr<Socket> s) {
    if(!this->isLoggedIn(s)) {
        this->proto.write(s, cmmp::OutputOneResponsePacket(false, "Not logged in"));
        return;
    }

    {
        Lock lk(this->parcLocationsMutex);
        for(ParcLocation& location : this->parcLocations) {
            if(location.containerId == p.getContainerId() && location.flag == ParkLocationFlag::Leaving) {
                // The location is not removed from the file, only set free
                location.containerId.clear();
                location.flag = ParkLocationFlag::Free;
                this->containerFile.update(location, [&location](const ParcLocation& file_location) {
                    return location.x == file_location.x && location.y == file_location.y;
                });
                this->proto.write(s, cmmp::OutputOneResponsePacket(true));
                return;
            }
        }
    }

    LOG << Logger::Warning << "Invalid container id output: " << p.getContainerId();
    this->proto.write(s, cmmp::OutputOneResponsePacket(false, "Invalid container id (not existing or ready to leave)"));
}

void ContainerServer::outputDoneHandler(const cmmp::OutputDonePacket&, std::shared_ptr<Socket> s) {
    if(!this->isLoggedIn(s)) {
        this->proto.write(s, cmmp::OutputDoneResponsePacket(false, "Not logged in"));
        return;
    }

    // TODO mkay? :/ That's some useless stuff here
    this->proto.write(s, cmmp::OutputDoneResponsePacket(true));
}

void ContainerServer::logoutHandler(const cmmp::LogoutPacket&, std::shared_ptr<Socket> s) {
    if(!this->isLoggedIn(s)) {
        this->proto.write(s, cmmp::LogoutResponsePacket(false, "Not logged in"));
        return;
    }

    // TODO Not sure what the stuff from the packet is useful for...
    Lock lk(this->loggedInUsersMutex);
    this->loggedInUsers.erase(s.get());
    this->proto.write(s, cmmp::LogoutResponsePacket(true, "Logged out"));

    // s->close(); // Not closing the connection so he can reconnect without restarting the application
}

bool ContainerServer::isLoggedIn(std::shared_ptr<Socket> socket) {
    Lock lk(this->loggedInUsersMutex);
    return this->loggedInUsers.find(socket.get()) != this->loggedInUsers.end();
}

bool ContainerServer::findFreePlace(Container& container) {
    bool changed;
    do {
        changed = false;

        for(ParcLocation& location : this->parcLocations) {
            if(location.flag == ParkLocationFlag::Taken && location.containerId == container.id) {
                throw std::logic_error("Container '" + container.id + "' is already stored "
                    "in the parc at " + std::to_string(location.x) + ":" + std::to_string(location.y));
                return false;
            }

            if(location.flag == ParkLocationFlag::Free
                    || (location.flag == ParkLocationFlag::Reserved && location.containerId == container.id)) {
                LOG << Logger::Debug << "Found a free/reserved place in " << location.x << ':' << location.y;
                container.x = location.x;
                container.y = location.y;
                location.flag = ParkLocationFlag::Storing;
                return true;
            }

            if(location.x == container.x && location.y == container.y) {
                changed = true;
                ++container.x;
                if(container.x > 200) { // TODO Put a real value here
                    container.x = 0;
                    ++container.y;
                    if(container.y > 200) { // TODO Put a real value here
                        return false;
                    }
                }
            }
            // TODO Am pretty sure this doesn't actually always works
        }
    } while(changed);

    LOG << Logger::Debug << "Found a place not in the file " << container.x << ':' << container.y;
    this->parcLocations.emplace_back(ParcLocation {
        container.x,
        container.y,
        container.id,
        ParkLocationFlag::Storing,
        "now",                     // TODO Need to be handled by the db
        "now",                     // TODO Need to be handled by the db
        0,                         // TODO Weight is not clearly defined, I'd just remove it all together
        container.destination,
        MeanOfTransportation::Boat // TODO Ask if this really matters (as long as it gets to the destination, it's ok no?)
    });

    return true;
}

void ContainerServer::cleanupContainersBeingStored(Socket& s, Socket::CloseReason) {
    Lock lk(this->containersBeingStoredMutex);
    auto it = this->containersBeingStored.find(&s);

    if(it != this->containersBeingStored.end()) {
        Lock lk(this->parcLocationsMutex);
        LOG << Logger::Warning << "Removing user currently storing containers";

        for(Container& container : it->second) {
            auto to_fix = std::find_if(
                this->parcLocations.begin(),
                this->parcLocations.end(),
                [&container](const ParcLocation& location) {
                    return location.x == container.x && location.y == container.y;
                }
            );

            if(to_fix != this->parcLocations.end()) {
                to_fix->flag = ParkLocationFlag::Free;
            }
        }
        this->containersBeingStored.erase(&s);
    }
}
