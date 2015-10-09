#ifndef CONTAINER_SERVER_CONTAINERCLIENT_HPP
#define CONTAINER_SERVER_CONTAINERCLIENT_HPP

#include <cassert>
#include <memory>
#include <string>

#include "cmmp/CMMPTranslator.hpp"
#include "input/InputHelper.hpp"
#include "net/Socket.hpp"
#include "protocol/ProtocolHandler.hpp"

class ContainerClient {

public:
    ContainerClient(std::shared_ptr<Socket>, ProtocolHandler<CMMPTranslator, PacketId>&);

    ContainerClient& init();
    ContainerClient& mainLoop();

private:
    std::shared_ptr<Socket> socket;
    ProtocolHandler<CMMPTranslator, PacketId>& proto;
    bool closed;
    bool loggedIn;

    void loginMenu();
    void login(bool);
    void menu();

};

#endif
