#include "ContainerServer.hpp"

ContainerServer::ContainerServer(unsigned short port, ThreadPool& pool)
        : pool(pool),
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
void debugHandler(const T& p, Socket& s) {
    LOG << Logger::Debug << "Received packet: " << (int) p.getId() << " from " << s.getHandle();
}
ContainerServer& ContainerServer::init() {
    LoginPacket::registerHandler(debugHandler<LoginPacket>);
    InputTruckPacket::registerHandler(debugHandler<InputTruckPacket>);
    InputDonePacket::registerHandler(debugHandler<InputDonePacket>);
    OutputReadyPacket::registerHandler(debugHandler<OutputReadyPacket>);
    OutputOnePacket::registerHandler(debugHandler<OutputOnePacket>);
    OutputDonePacket::registerHandler(debugHandler<OutputDonePacket>);
    LogoutPacket::registerHandler(debugHandler<LogoutPacket>);

    LogoutPacket::registerHandler([this](LogoutPacket, Socket& s) {
        s.close();
    });

    return *this;
}

ContainerServer& ContainerServer::listen() {
    while(!closed) {
        try {
            std::lock_guard<std::mutex>(this->connectionsMutex);
            Socket connection = socket.accept();
            LOG << Logger::Debug << "Connection accepted " << connection.getHandle();
            this->selector.addSocket(connection);
        } catch(IOError e) {
            LOG << Logger::Error << "IOError: " << e.what();
            continue;
        }

        // TODO Refresh selector
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
