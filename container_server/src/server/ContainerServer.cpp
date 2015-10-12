#include "server/ContainerServer.hpp"

ContainerServer::ContainerServer(unsigned short port, const string& container_file, const string& user_file, ThreadPool& pool)
        : containerFile(container_file),
          parcLocations(containerFile.load()),
          users(user_file, ';'),
          pool(pool),
          proto(CMMPTranslator()),
          socket(),
          selector(),
          selectorThread(selector, pool, proto),
          closed(false) {
    LOG << Logger::Debug << "Loaded " << parcLocations.size() << " location from " << container_file;

    LOG << "Binding server socket to " << port;
    socket.bind(port);
}

ContainerServer::~ContainerServer() {
    this->close();
}

ContainerServer& ContainerServer::init() {
    LoginPacket::registerHandler(std::bind(&ContainerServer::loginHandler, this, _1, _2));
    InputTruckPacket::registerHandler(std::bind(&ContainerServer::inputTruckHandler, this, _1, _2));
    InputDonePacket::registerHandler(std::bind(&ContainerServer::inputDoneHandler, this, _1, _2));
    OutputReadyPacket::registerHandler(std::bind(&ContainerServer::outputReadyHandler, this, _1, _2));
    OutputOnePacket::registerHandler(std::bind(&ContainerServer::outputOneHandler, this, _1, _2));
    OutputDonePacket::registerHandler(std::bind(&ContainerServer::outputDoneHandler, this, _1, _2));
    LogoutPacket::registerHandler(std::bind(&ContainerServer::logoutHandler, this, _1, _2));

    return *this;
}

ContainerServer& ContainerServer::listen() {
    while(!closed) {
        try {
            std::shared_ptr<Socket> connection = socket.accept();
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

void ContainerServer::loginHandler(const LoginPacket& p, std::shared_ptr<Socket> s) {
    string username = p.getUsername();
    if(this->isLoggedIn(s)) {
        LOG << Logger::Warning << "Ignoring connect attempt from an authentified socket: " << username;

        this->proto.write(s, LoginResponsePacket(false, "Already logged in"));
        return;
    }

    string password = p.getPassword();

    if(p.isNew()) {
        if(username.empty() || password.empty()) {
            this->proto.write(s, LoginResponsePacket(false, "Empty username or password"));
            return;
        }

        if(username.find_first_of("\n;\r\0") != string::npos
                || password.find_first_of("\n;\r\0") != string::npos
                || std::count_if(username.begin(), username.end(), [](char c) { return !std::isprint(c); }) != 0
                || std::count_if(password.begin(), password.end(), [](char c) { return !std::isprint(c); }) != 0) {
            this->proto.write(s, LoginResponsePacket(false, "Invalid character in username or password"));
            return;
        }

        Lock usersLock(this->usersMutex);
        if(!this->users.find("username", username).empty()) {
            this->proto.write(s, LoginResponsePacket(false, "Username already in use"));
            return;
        }

        // Insert and save
        this->users.insert({username, password});
        this->users.save();

        // Login
        Lock loggedInUsersLock(this->loggedInUsersMutex);
        this->loggedInUsers.insert({ s.get(), username});

        this->proto.write(s, LoginResponsePacket(true));
        return;
    }

    Lock lk(this->usersMutex);
    std::map<string, string> map = this->users.find("username", username);
    if(map.empty()) {
        LOG << Logger::Warning << "Tried to login with unknown username: " << username;

        this->proto.write(s, LoginResponsePacket(false, "user not found"));
        return;
    }

    if(map.begin()->second == password) {
        LOG << username << " logged in";

        Lock lk(this->loggedInUsersMutex);
        this->loggedInUsers.insert({ s.get(), username});
        this->proto.write(s, LoginResponsePacket(true));
        return;
    }

    LOG << Logger::Warning << "Tried to login from " << username << " with invalid password";
    this->proto.write(s, LoginResponsePacket(false, "Invalid password"));
}

void ContainerServer::inputTruckHandler(const InputTruckPacket& p, std::shared_ptr<Socket> s) {
    if(!this->isLoggedIn(s)) {
        this->proto.write(s, InputTruckResponsePacket(false, {}, "Not logged in"));
        return;
    }

    {
        // Check a the client isn't already storing containers
        Lock lk(this->containersBeingStoredMutex);
        if(this->containersBeingStored.find(s.get()) != this->containersBeingStored.end()) {
            this->proto.write(s, InputTruckResponsePacket(false, {}, "Already storing containers"));
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
                this->proto.write(s, InputTruckResponsePacket(false, {}, string("Something is really bad: ") + e.what()));
                return;
            }

            if(!had_place) {
                this->proto.write(s, InputTruckResponsePacket(false, {}, "No free place available"));
                return;
            } else {
                // Save the container info for later
                containers.emplace_back(tmp);
            }
        }

        this->containersBeingStored.insert({ s.get(), containers });
    }

    this->proto.write(s, InputTruckResponsePacket(true, containers));
}

void ContainerServer::inputDoneHandler(const InputDonePacket& p, std::shared_ptr<Socket> s) {
    if(!this->isLoggedIn(s)) {
        this->proto.write(s, InputDoneResponsePacket(false, "Not logged in"));
        return;
    }

    Lock lk(this->containersBeingStoredMutex);
    auto containersToValidate = this->containersBeingStored.find(s.get());
    if(containersToValidate == this->containersBeingStored.end()) {
        LOG << Logger::Warning << "Received InputDone even tho no containers where being stored";
        this->proto.write(s, InputDoneResponsePacket(false, "No containers currently being stored"));
        return;
    }

    if(!p.isOk()) {
        LOG << Logger::Debug << "Something happened while storing, cleaning up";
        this->cleanupContainersBeingStored(*s, Socket::CloseReason::Error);
        this->proto.write(s, InputDoneResponsePacket(true));
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
    this->proto.write(s, InputDoneResponsePacket(true));
}

void ContainerServer::outputReadyHandler(const OutputReadyPacket& p, std::shared_ptr<Socket> s) {
    if(!this->isLoggedIn(s)) {
        this->proto.write(s, OutputReadyResponsePacket(false, {}, "Not logged in"));
        return;
    }

    uint16_t capacity = p.getCapacity();
    if(capacity == 0 || p.getDestination().empty()) {
        this->proto.write(s, OutputReadyResponsePacket(false, {}, "Invalid value received"));
        return;
    }

    LOG << Logger::Debug
        << "Transport '" << p.getLicense()
        << "' going to '" << p.getDestination()
        << "' can carry " << capacity << " container(s)";

    std::vector<string> containers;
    {
        Lock lk(this->parcLocationsMutex);

        for(ParcLocation& location : this->parcLocations) {
            if(location.destination == p.getDestination()) {
                containers.emplace_back(location.containerId);
                location.flag = ParkLocationFlag::Leaving;

                if(--capacity == 0) {
                    break;
                }
            }
        }
    }

    if(containers.empty()) {
        this->proto.write(s, OutputReadyResponsePacket(false, {}, "No containers for this destination"));
    } else {
        this->proto.write(s, OutputReadyResponsePacket(true, containers));
    }

}

void ContainerServer::outputOneHandler(const OutputOnePacket& p, std::shared_ptr<Socket> s) {
    if(!this->isLoggedIn(s)) {
        this->proto.write(s, OutputOneResponsePacket(false, "Not logged in"));
        return;
    }

    bool found = false;
    {
        Lock lk(this->parcLocationsMutex);
        for(ParcLocation& location : this->parcLocations) {
            if(location.containerId == p.getContainerId() && location.flag == ParkLocationFlag::Leaving) {
                location.flag = ParkLocationFlag::Free;
                found = true;
                break;
            }
        }

        if(found) {
            this->containerFile.save(this->parcLocations.begin(), this->parcLocations.end());
            this->proto.write(s, OutputOneResponsePacket(true));
            return;
        }
    }

    this->proto.write(s, OutputOneResponsePacket(false, "Invalid container id (not existing or ready to leave)"));
}

void ContainerServer::outputDoneHandler(const OutputDonePacket&, std::shared_ptr<Socket> s) {
    if(!this->isLoggedIn(s)) {
        this->proto.write(s, OutputDoneResponsePacket(false, "Not logged in"));
        return;
    }

    // TODO mkay? :/ That's some useless stuff here
    this->proto.write(s, OutputDoneResponsePacket(true));
}

void ContainerServer::logoutHandler(const LogoutPacket&, std::shared_ptr<Socket> s) {
    if(!this->isLoggedIn(s)) {
        this->proto.write(s, LogoutResponsePacket(false, "Not logged in"));
        return;
    }

    // TODO Not sure what the stuff from the packet is useful for...
    Lock lk(this->loggedInUsersMutex);
    this->loggedInUsers.erase(s.get());
    this->proto.write(s, LogoutResponsePacket(true, "Logged out"));

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
