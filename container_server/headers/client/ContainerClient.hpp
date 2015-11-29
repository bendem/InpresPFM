#ifndef CONTAINER_SERVER_CONTAINERCLIENT_HPP
#define CONTAINER_SERVER_CONTAINERCLIENT_HPP

#include <cassert>
#include <memory>
#include <string>

#include "cmmp/Translator.hpp"
#include "input/InputHelper.hpp"
#include "net/Socket.hpp"
#include "protocol/ProtocolHandler.hpp"

using namespace cmmp;

class ContainerClient {
    using string = std::string;
    using cstring_ref = const string&;

public:
    ContainerClient(std::shared_ptr<Socket>, ProtocolHandler<Translator, PacketId>&);

    ContainerClient& init();
    ContainerClient& mainLoop();

private:
    std::shared_ptr<Socket> socket;
    ProtocolHandler<Translator, PacketId>& proto;
    bool closed;
    bool loggedIn;

    void loginMenu();
    void menu();

};

#endif
