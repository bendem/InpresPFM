#include "ContainerServer.hpp"

ContainerServer::ContainerServer(unsigned short port, ThreadPool& pool)
        : pool(pool),
          proto(CMMPTranslator()),
          socket(),
          pollingThread([this] {
              while(!this->closed) {
                  for(auto socket : this->selector.select()) {
                      this->pool.submit([this, socket] {
                          this->proto.read(*socket);
                      });
                  }
              }
          }) {
    socket.bind(port);
}

ContainerServer::~ContainerServer() {
    this->close();
}

ContainerServer& ContainerServer::init() {
    //auto debugHandler = [](const Packet<T>& p) {
    //    LOG << Logger::Debug << "Received packet: " << (int) p.getId();
    //};

    //LoginPacket::registerHandler(debugHandler);
    //InputTruckPacket::registerHandler(debugHandler);
    //InputDonePacket::registerHandler(debugHandler);
    //OutputReadyPacket::registerHandler(debugHandler);
    //OutputOnePacket::registerHandler(debugHandler);
    //OutputDonePacket::registerHandler(debugHandler);
    //LogoutPacket::registerHandler(debugHandler);

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
