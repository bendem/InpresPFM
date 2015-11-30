#ifndef CONTAINER_SERVER_CONTAINERCLIENT_HPP
#define CONTAINER_SERVER_CONTAINERCLIENT_HPP

#include <cassert>
#include <memory>
#include <string>
#include <condition_variable>

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
    void pause(bool);

private:
    std::shared_ptr<Socket> socket;
    ProtocolHandler<Translator, PacketId>& proto;
    bool closed;
    bool loggedIn;
    std::atomic_bool paused;
    std::mutex pausedMutex;
    std::condition_variable pausedCond;

    void loginMenu();
    void menu();
    template<class P>
    void send(std::shared_ptr<Socket>, const P&);

};

#endif
