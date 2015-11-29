#ifndef CONTAINER_SERVER_CSA_LISTPACKET_HPP
#define CONTAINER_SERVER_CSA_LISTPACKET_HPP

#include "csa/PacketId.hpp"
#include "protocol/Packet.hpp"

namespace csa {

class ListPacket : public Packet<ListPacket> {

public:
    static const PacketId id = PacketId::List;

    static ListPacket decode(std::istream&) { return ListPacket(); }
    void encode(std::ostream&) const {}

};

}

#endif
