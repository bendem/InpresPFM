#ifndef CONTAINER_SERVER_CONTAINER_HPP
#define CONTAINER_SERVER_CONTAINER_HPP

#include <string>

#include "cmmp/PacketId.hpp"
#include "protocol/Packet.hpp"

struct Container {
    std::string id;
    std::string destination;
    uint32_t x;
    uint32_t y;
};

#endif
