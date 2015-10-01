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
            std::lock_guard<std::mutex>(this->connectionsMutex);
            std::shared_ptr<Socket> connection = socket.accept();
            LOG << Logger::Debug << "Connection accepted " << connection->getHandle();
            connection->registerCloseHandler([](Socket& s, Socket::CloseReason) {
                LOG << "Close handler for " << s.getHandle();
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
    // TODO Handle p.isNew()

    std::map<std::string, std::string> map = this->users.find("username", p.getUsername());
    if(map.empty()) {
        LOG << Logger::Warning << "Tried to login with unknown username: " << p.getUsername();
        this->proto.write(s, LoginResponsePacket(false, "user not found"));
        return;
    }

    if(map.begin()->second == p.getPassword()) {
        LOG << p.getUsername() << " logged in";
        this->proto.write(s, LoginResponsePacket(true, ""));
        return;
    }

    LOG << Logger::Warning << "Tried to login from " << p.getUsername() << " with invalid password";
    this->proto.write(s, LoginResponsePacket(false, "Invalid password"));
}

void ContainerServer::inputTruckHandler(const InputTruckPacket& p, std::shared_ptr<Socket> s) {
    // TODO Implement inputTruckHandler
}

void ContainerServer::inputDoneHandler(const InputDonePacket& p, std::shared_ptr<Socket> s) {
    // TODO Implement inputDoneHandler
}

void ContainerServer::outputReadyHandler(const OutputReadyPacket& p, std::shared_ptr<Socket> s) {
    // TODO Implement outputReadyHandler
}

void ContainerServer::outputOneHandler(const OutputOnePacket& p, std::shared_ptr<Socket> s) {
    // TODO Implement outputOneHandler
}

void ContainerServer::outputDoneHandler(const OutputDonePacket& p, std::shared_ptr<Socket> s) {
    // TODO Implement outputDoneHandler
}

void ContainerServer::logoutHandler(const LogoutPacket& p, std::shared_ptr<Socket> s) {
    // TODO Implement logoutHandler
}
