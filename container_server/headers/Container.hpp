#ifndef CONTAINER_SERVER_CONTAINER_HPP
#define CONTAINER_SERVER_CONTAINER_HPP

#include <string>

#include "cmmp/PacketId.hpp"
#include "io/StreamUtils.hpp"
#include "protocol/Packet.hpp"

struct Container {
    std::string id;
    std::string destination;
    uint16_t x;
    uint16_t y;
};

template<> std::ostream& StreamUtils::write<Container>(std::ostream&, const Container&);
template<> Container StreamUtils::read<Container>(std::istream&);

#endif
