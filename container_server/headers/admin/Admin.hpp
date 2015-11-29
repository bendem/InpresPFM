#ifndef CONTAINER_SERVER_ADMIN_HPP
#define CONTAINER_SERVER_ADMIN_HPP

#include "csa/PacketId.hpp"
#include "csa/Translator.hpp"
#include "server/ContainerServer.hpp"

using namespace csa;

class Admin {

public:
    Admin(ContainerServer&, unsigned short);

    void run();

private:
    ContainerServer& server;
    ProtocolHandler<Translator, csa::PacketId> protocolHandler;
    Socket socket;
    std::thread thread;
    std::atomic_bool closed;

};

#endif
