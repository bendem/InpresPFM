#include "ContainerServer.hpp"

ContainerServer::ContainerServer(unsigned short port, CSVFile& users, ThreadPool& pool)
        : users(users),
          pool(pool),
          proto(CMMPTranslator()),
          socket(),
          selector(),
          selectorThread(selector, pool, proto),
          closed(false) {
    LOG << "Binding server socket to " << port;
    socket.bind(port);
}

ContainerServer::~ContainerServer() {
    this->close();
}

template<class T>
void debugHandler(const T& p, std::shared_ptr<Socket> s) {
    LOG << Logger::Debug << "Received packet: " << (int) p.getId() << " from " << s->getHandle();
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
    {
        std::lock_guard<std::mutex> lk(this->loggedInUsersMutex);
        if(this->loggedInUsers.find(s.get()) != this->loggedInUsers.end()) {
            LOG << Logger::Warning << "Ignoring connect attempt from an authentified socket: " << p.getUsername();
            this->proto.write(s, LoginResponsePacket(false, "Already logged in"));
            return;
        }
    }

    if(p.isNew()) {
        // TODO Handle p.isNew()
        LOG << Logger::Error << "User creation not implemented";
        this->proto.write(s, LoginResponsePacket(false, "User creation not implemented"));
        return;
    }

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
        this->proto.write(s, LoginResponsePacket(true, ""));
        return;
    }

    LOG << Logger::Warning << "Tried to login from " << p.getUsername() << " with invalid password";
    this->proto.write(s, LoginResponsePacket(false, "Invalid password"));
}

void ContainerServer::inputTruckHandler(const InputTruckPacket&, std::shared_ptr<Socket>) {
    // TODO Implement inputTruckHandler
}

void ContainerServer::inputDoneHandler(const InputDonePacket&, std::shared_ptr<Socket>) {
    // TODO Implement inputDoneHandler
}

void ContainerServer::outputReadyHandler(const OutputReadyPacket&, std::shared_ptr<Socket>) {
    // TODO Implement outputReadyHandler
}

void ContainerServer::outputOneHandler(const OutputOnePacket&, std::shared_ptr<Socket>) {
    // TODO Implement outputOneHandler
}

void ContainerServer::outputDoneHandler(const OutputDonePacket&, std::shared_ptr<Socket>) {
    // TODO Implement outputDoneHandler
}

void ContainerServer::logoutHandler(const LogoutPacket&, std::shared_ptr<Socket> s) {
    // TODO Not sure what the stuff from the packet is useful for...
    std::lock_guard<std::mutex> lk(this->loggedInUsersMutex);
    this->loggedInUsers.erase(s.get());
    s->close();
}
