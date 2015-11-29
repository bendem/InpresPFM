#ifndef CONTAINER_SERVER_CSA_LISTRESPONSEPACKET_HPP
#define CONTAINER_SERVER_CSA_LISTRESPONSEPACKET_HPP

#include "csa/PacketId.hpp"
#include "protocol/Packet.hpp"

namespace csa {

class ListResponsePacket : public Packet<ListResponsePacket> {

public:
    static const PacketId id = PacketId::ListResponse;

    ListResponsePacket(std::vector<std::string> ips) : ips(ips) { }

    static ListResponsePacket decode(std::istream&);
    void encode(std::ostream&) const;

private:
    std::vector<std::string> ips;

};

}

#endif
