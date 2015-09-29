#ifndef CPP_COMMONS_SELECTOR_HPP
#define CPP_COMMONS_SELECTOR_HPP

#include <unordered_map>

#include "net/Socket.hpp"

class Selector {

public:
    Selector();
    ~Selector();

    Selector& addSocket(std::shared_ptr<Socket>);
    Selector& removeSocket(std::shared_ptr<Socket>);

    std::vector<std::shared_ptr<Socket>> select();

    void interrupt() const;

private:
    std::array<int, 2> pipes;
    std::unordered_map<int, std::shared_ptr<Socket>> sockets;
    std::mutex socketsMutex;

};

#endif
