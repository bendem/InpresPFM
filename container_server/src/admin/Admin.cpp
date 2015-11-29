#include "admin/Admin.hpp"

Admin::Admin(ContainerServer& server, unsigned short port)
    : server(server),
      closed(false) {
    LOG << "Binding admin socket to " << port;
    socket.bind(port);
    thread = std::thread(&Admin::run, this);
}

void Admin::run() {
    LOG << Logger::Debug << "Admin running";
    while(!closed) {
        std::shared_ptr<Socket> client = socket.accept();
        LOG << "Admin connection from " << client->getHost() << ':' << client->getPort();
        // TODO Handle stuff on another thread and send all other connections to hell
        // FIXME Wrong packet, introduce csa namespace!
        const LoginPacket loginPacket = protocolHandler.readSpecificPacket<LoginPacket>(client);
        //loginPacket.
    }
}
