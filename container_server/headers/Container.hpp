#ifndef CONTAINER_SERVER_CONTAINER_HPP
#define CONTAINER_SERVER_CONTAINER_HPP

#include <string>

#include "cmmp/PacketId.hpp"
#include "protocol/Packet.hpp"

struct Container {
    std::string id;
    std::string destination;
    uint16_t x;
    uint16_t y;
};

#endif
