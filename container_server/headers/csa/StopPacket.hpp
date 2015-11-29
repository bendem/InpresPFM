#ifndef CONTAINER_SERVER_CSA_STOPPACKET_HPP
#define CONTAINER_SERVER_CSA_STOPPACKET_HPP

#include "csa/PacketId.hpp"
#include "protocol/Packet.hpp"

namespace csa {

class StopPacket : public Packet<StopPacket> {

public:
    static const PacketId id = PacketId::Stop;

    StopPacket(uint32_t time) : time(time) {}

    uint32_t getTime() const { return time; }

    static StopPacket decode(std::istream& is) {
        return StopPacket(StreamUtils::read<uint32_t>(is));
    }
    void encode(std::ostream& os) const {
        StreamUtils::write(os, time);
    }

private:
    uint32_t time;

};

}

#endif
