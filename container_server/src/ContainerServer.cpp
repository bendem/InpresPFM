#include "ContainerServer.hpp"

ContainerServer::ContainerServer(unsigned short port, ThreadPool& pool)
        : pool(pool),
          proto(CMMPTranslator()),
          socket(),
          pollingThread([this] {
              LOG << "Starting polling thread";
              while(!this->closed) {
                  for(auto socket : this->selector.select()) {
                      this->pool.submit([this, socket] {
                          this->proto.read(*socket);
                      });
                  }
              }
          }),
          closed(false) {
    LOG << "Binding server socket to " << port;
    socket.bind(port);
}

ContainerServer::~ContainerServer() {
    this->close();
}

template<class T>
void debugHandler(const T& p) {
    LOG << Logger::Debug << "Received packet: " << (int) p.getId();
}
ContainerServer& ContainerServer::init() {
    LoginPacket::registerHandler(debugHandler<LoginPacket>);
    InputTruckPacket::registerHandler(debugHandler<InputTruckPacket>);
    InputDonePacket::registerHandler(debugHandler<InputDonePacket>);
    OutputReadyPacket::registerHandler(debugHandler<OutputReadyPacket>);
    OutputOnePacket::registerHandler(debugHandler<OutputOnePacket>);
    OutputDonePacket::registerHandler(debugHandler<OutputDonePacket>);
    LogoutPacket::registerHandler(debugHandler<LogoutPacket>);

    return *this;
}

ContainerServer& ContainerServer::listen() {
    while(!closed) {
        try {
            std::lock_guard<std::mutex>(this->connectionsMutex);
            connections.push_back(socket.accept());
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
