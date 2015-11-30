#ifndef CONTAINER_SERVER_URGENCYSERVER_HPP
#define CONTAINER_SERVER_URGENCYSERVER_HPP

#include "io/StreamUtils.hpp"
#include "net/Socket.hpp"

class UrgencyServer {

public:
    UrgencyServer(unsigned short);
    ~UrgencyServer();

    void accept();
    UrgencyServer& send(const std::string&);
    void close();

private:
    Socket socket;
    std::thread thread;
    std::atomic_bool closed;
    std::mutex socketsMutex;
    std::vector<std::shared_ptr<Socket>> sockets;

};

#endif
