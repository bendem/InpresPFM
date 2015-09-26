#ifndef CONTAINER_SERVER_SELECTOR_HPP
#define CONTAINER_SERVER_SELECTOR_HPP

#include <map>

#include "net/Socket.hpp"

class Selector {

public:
    Selector();

    Selector& addSocket(Socket&);
    Selector& removeSocket(Socket&);

    std::vector<Socket*> select();

private:
    std::map<int, Socket*> sockets; // weak pointers

};

#endif //CONTAINER_SERVER_SELECTOR_HPP
