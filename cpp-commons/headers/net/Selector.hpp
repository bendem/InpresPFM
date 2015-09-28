#ifndef CONTAINER_SERVER_SELECTOR_HPP
#define CONTAINER_SERVER_SELECTOR_HPP

#include <unordered_map>

#include "net/Socket.hpp"

class Selector {

public:
    Selector();
    ~Selector();

    Selector& addSocket(Socket);
    Selector& removeSocket(const Socket&);

    std::vector<Socket> select();

    void interrupt() const;

private:
    std::array<int, 2> pipes;
    std::unordered_map<int, Socket> sockets;
    std::mutex socketsMutex;

};

#endif //CONTAINER_SERVER_SELECTOR_HPP
