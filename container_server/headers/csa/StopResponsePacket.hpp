#ifndef CONTAINER_SERVER_CSA_STOPRESPONSEPACKET_HPP
#define CONTAINER_SERVER_CSA_STOPRESPONSEPACKET_HPP

#include "csa/PacketId.hpp"
#include "protocol/Packet.hpp"

namespace csa {

class StopResponsePacket : public Packet<StopResponsePacket> {

public:
    static const PacketId id = PacketId::StopResponse;

    StopResponsePacket(const std::string& error) : error(error) {}

    const std::string& getError() const { return error; }

    static StopResponsePacket decode(std::istream& is) {
        return StopResponsePacket(StreamUtils::read<std::string>(is));
    }
    void encode(std::ostream& os) const {
        StreamUtils::write(os, error);
    }

private:
    std::string error;

};

}

#endif
