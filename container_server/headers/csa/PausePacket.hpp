#ifndef CONTAINER_SERVER_CSA_PAUSEPACKET_HPP
#define CONTAINER_SERVER_CSA_PAUSEPACKET_HPP

#include "csa/PacketId.hpp"
#include "protocol/Packet.hpp"

namespace csa {

class PausePacket : public Packet<PausePacket> {

public:
    static const PacketId id = PacketId::Pause;

    static PausePacket decode(std::istream&) { return PausePacket(); }
    void encode(std::ostream&) const {}

};

}

#endif
