#ifndef CONTAINER_SERVER_SELECTOR_HPP
#define CONTAINER_SERVER_SELECTOR_HPP

#include <map>

#include "net/Socket.hpp"

class Selector {

public:
    Selector();

    Selector& addSocket(Socket&);
    Selector& removeSocket(Socket&);

    std::vector<std::weak_ptr<Socket>> select();

private:
    std::map<int, std::weak_ptr<Socket>> sockets;

};

#endif //CONTAINER_SERVER_SELECTOR_HPP
