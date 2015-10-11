#include "server/ContainerServer.hpp"

ContainerServer::ContainerServer(unsigned short port, const std::string& container_file, const std::string& user_file, ThreadPool& pool)
        : containerFile(container_file),
          parcLocations(containerFile.load()),
          users(user_file, ';'),
          pool(pool),
          proto(CMMPTranslator()),
          socket(),
          selector(),
          selectorThread(selector, pool, proto),
          closed(false) {
    LOG << Logger::Debug << "Loaded " << parcLocations.size() << " location";

    LOG << "Binding server socket to " << port;
    socket.bind(port);
}

ContainerServer::~ContainerServer() {
    this->close();
}

ContainerServer& ContainerServer::init() {
    using std::placeholders::_1;
    using std::placeholders::_2;

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
            LOG << Logger::Debug << "Connection accepted " << connection->getHandle();

            connection->registerCloseHandler([this](Socket& s, Socket::CloseReason) {
                std::lock_guard<std::mutex> lk(this->loggedInUsersMutex);
                if(this->loggedInUsers.erase(&s) == 1) {
                    LOG << Logger::Debug << "Removed connected user, connection closed";
                }
            });
            this->selector.addSocket(connection);
        } catch(IOError e) {
            LOG << Logger::Error << "IOError: " << e.what();
            continue;
        }
    }

    return *this;
}

void ContainerServer::close() {
    if(this->closed.exchange(true)) {
        return;
    }
    // TODO Close more?
    // TODO Stop accept
}

void ContainerServer::loginHandler(const LoginPacket& p, std::shared_ptr<Socket> s) {
    if(this->isLoggedIn(s)) {
        LOG << Logger::Warning << "Ignoring connect attempt from an authentified socket: " << p.getUsername();

        this->proto.write(s, LoginResponsePacket(false, "Already logged in"));
        return;
    }

    if(p.isNew()) {
        if(p.getUsername().empty() || p.getPassword().empty()) {
            this->proto.write(s, LoginResponsePacket(false, "Empty username or password"));
            return;
        }

        if(p.getUsername().find_first_of("\n;\r\0") != std::string::npos
                || p.getPassword().find_first_of("\n;\r\0") != std::string::npos) {
            this->proto.write(s, LoginResponsePacket(false, "Invalid character in username"));
            return;
        }

        std::lock_guard<std::mutex> usersLock(this->usersMutex);
        if(!this->users.find("username", p.getUsername()).empty()) {
            this->proto.write(s, LoginResponsePacket(false, "Username already in use"));
            return;
        }

        // Insert and save
        this->users.insert({ p.getUsername(), p.getPassword() });
        this->users.save();

        // Login
        std::lock_guard<std::mutex> loggedInUsersLock(this->loggedInUsersMutex);
        this->loggedInUsers.insert({ s.get(), p.getUsername() });

        this->proto.write(s, LoginResponsePacket(true));
        return;
    }

    std::lock_guard<std::mutex> lk(this->usersMutex);
    std::map<std::string, std::string> map = this->users.find("username", p.getUsername());
    if(map.empty()) {
        LOG << Logger::Warning << "Tried to login with unknown username: " << p.getUsername();

        this->proto.write(s, LoginResponsePacket(false, "user not found"));
        return;
    }

    if(map.begin()->second == p.getPassword()) {
        LOG << p.getUsername() << " logged in";

        std::lock_guard<std::mutex> lk(this->loggedInUsersMutex);
        this->loggedInUsers.insert({ s.get(), p.getUsername() });
        this->proto.write(s, LoginResponsePacket(true));
        return;
    }

    LOG << Logger::Warning << "Tried to login from " << p.getUsername() << " with invalid password";
    this->proto.write(s, LoginResponsePacket(false, "Invalid password"));
}

void ContainerServer::inputTruckHandler(const InputTruckPacket& p, std::shared_ptr<Socket> s) {
    if(!this->isLoggedIn(s)) {
        this->proto.write(s, InputTruckResponsePacket(false, {}, "Not logged in"));
        return;
    }

    std::vector<Container> containers(p.getContainers());
    LOG << "Received " << containers.size() << " containers";

    {
        std::lock_guard<std::mutex> lk(this->parcLocationsMutex);
        for(Container& container : containers) {
            bool had_place = false;
            try {
                had_place = this->findFreePlace(container);
            } catch(std::logic_error e) {
                LOG << Logger::Error << "Something is really bad: " << e.what();
            } catch(std::runtime_error e) {
                LOG << Logger::Error << "Could not find a free place: " << e.what();
            }

            if(!had_place) {
                this->proto.write(s, InputTruckResponsePacket(false, {}, "No free place available"));
                return;
            }
        }
    }

    this->proto.write(s, InputTruckResponsePacket(true, containers));
}

void ContainerServer::inputDoneHandler(const InputDonePacket&, std::shared_ptr<Socket> s) {
    if(!this->isLoggedIn(s)) {
        this->proto.write(s, InputDoneResponsePacket(false, "Not logged in"));
        return;
    }

    // TODO Do something with the weight
    this->proto.write(s, InputDoneResponsePacket(true, ""));
}

void ContainerServer::outputReadyHandler(const OutputReadyPacket& p, std::shared_ptr<Socket> s) {
    if(!this->isLoggedIn(s)) {
        this->proto.write(s, OutputReadyResponsePacket(false, {}, "Not logged in"));
        return;
    }

    std::vector<Container> containers;
    std::vector<std::string> containers_to_send;
    //int loaded = 0;
    LOG << "[OutputReadyHandler] Transport n° " << p.getLicense() << " going to " << p.getDestination() << " can carry " << p.getCapacity() << " containers.";

    /*containers = fileUtils.loadFile("FICH_PARC");
    for(auto cont : containers) {
        if(loaded < p.getCapacity()) {
            if(cont.getDestination() == p.getDestination()){
                containers_to_send.push_back(cont);
                loaded++;
            }
        } else {
            break;
        }
    }
    */
    if(containers_to_send.size()) {
        LOG << "[OutputReadyHandler] Sending " << containers_to_send.size() << " containers";
        this->proto.write(s, OutputReadyResponsePacket(true, containers_to_send, ""));
    } else {
        LOG << "[OutputReadyHandler] No containers available for " << p.getDestination();
        this->proto.write(s, OutputReadyResponsePacket(false, containers_to_send, "No containers for this destination."));
    }

}

void ContainerServer::outputOneHandler(const OutputOnePacket& p, std::shared_ptr<Socket> s) {
    if(!this->isLoggedIn(s)) {
        this->proto.write(s, OutputOneResponsePacket(false, "Not logged in"));
        return;
    }

    int test = 0;
    /* test = UtilsFich.deleteFromFile(p.getContainerId()); */

    if (test) {
        LOG << "[OutputOneHandler] Container " << p.getContainerId() << " is loaded";
        this->proto.write(s, OutputOneResponsePacket(true, ""));
    } else {
        LOG << "[OutputOneHandler] Container " << p.getContainerId() << " doesn't exist";
        this->proto.write(s, OutputOneResponsePacket(false, "Container doesn't exist."));
    }
}

void ContainerServer::outputDoneHandler(const OutputDonePacket& p, std::shared_ptr<Socket> s) {
    if(!this->isLoggedIn(s)) {
        this->proto.write(s, OutputDoneResponsePacket(false, "Not logged in"));
        return;
    }

    // TODO Add a way to remember the number of containers sent in OutputReadyHadler

    LOG << "[OutputDoneHandler] Loaded " << p.getContainerCount();

    this->proto.write(s, OutputDoneResponsePacket(true, ""));
}

void ContainerServer::logoutHandler(const LogoutPacket&, std::shared_ptr<Socket> s) {
    if(!this->isLoggedIn(s)) {
        this->proto.write(s, LogoutResponsePacket(false, "Not logged in"));
        return;
    }

    // TODO Not sure what the stuff from the packet is useful for...
    std::lock_guard<std::mutex> lk(this->loggedInUsersMutex);
    this->loggedInUsers.erase(s.get());
    this->proto.write(s, LogoutResponsePacket(true, "Logged out"));

    // s->close(); // Not closing the connection so he can reconnect without restarting the application
}

bool ContainerServer::isLoggedIn(std::shared_ptr<Socket> socket) {
    std::lock_guard<std::mutex> lk(this->loggedInUsersMutex);
    return this->loggedInUsers.find(socket.get()) != this->loggedInUsers.end();
}

bool ContainerServer::findFreePlace(Container& container) {
    bool changed;
    do {
        changed = false;

        for(ParcLocation& location : this->parcLocations) {
            if(location.flag == ParkLocationFlag::Taken && location.containerId == container.id) {
                // TODO Better exception? (as always)
                throw std::logic_error("Container '" + container.id + "' is already stored "
                    "in the parc at " + std::to_string(location.x) + ":" + std::to_string(location.y));
                return false;
            }

            if(location.flag == ParkLocationFlag::Free
                    || (location.flag == ParkLocationFlag::Reserved && location.containerId == container.id)) {
                LOG << Logger::Debug << "Found a free/reserved place in " << location.x << ':' << location.y;
                container.x = location.x;
                container.y = location.y;
                location.flag = ParkLocationFlag::Reserved;
                return true;
            }

            if(location.x == container.x && location.y == container.y) {
                changed = true;
                ++container.x;
                if(container.x > 200) { // TODO Put a real value here
                    container.x = 0;
                    ++container.y;
                    if(container.y > 200) { // TODO Put a real value here
                        throw std::runtime_error("Could not find any place to store " + container.id);
                        return false;
                    }
                }
            }
        }
    } while(changed);

    this->parcLocations.emplace_back(ParcLocation { container.x, container.y, container.id, ParkLocationFlag::Reserved });

    LOG << Logger::Debug << "Found a place not in the file " << container.x << ':' << container.y;
    return true;
}
