#include "cmmp/OutputOnePacket.hpp"

const PacketId OutputOnePacket::id = PacketId::OutputOne;

OutputOnePacket OutputOnePacket::decode(std::istream& is) {
    std::string container_id = StreamUtils::read<std::string>(is) ;

    return OutputOnePacket(container_id);
}

void OutputOnePacket::encode(std::ostream& os) const {
    StreamUtils::write(os, containerId);
}
