#ifndef CONTAINER_SERVER_CSA_PAUSERESPONSEPACKET_HPP
#define CONTAINER_SERVER_CSA_PAUSERESPONSEPACKET_HPP

#include "csa/PacketId.hpp"
#include "protocol/Packet.hpp"

namespace csa {

class PauseResponsePacket : public Packet<PauseResponsePacket> {

public:
    static const PacketId id = PacketId::PauseResponse;

    PauseResponsePacket(const std::string& error) : error(error) {}

    const std::string& getError() const { return error; }

    static PauseResponsePacket decode(std::istream& is) {
        return PauseResponsePacket(StreamUtils::read<std::string>(is));
    }
    void encode(std::ostream& os) const {
        StreamUtils::write(os, error);
    }

private:
    std::string error;

};

}

#endif
