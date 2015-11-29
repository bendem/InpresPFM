#include "cmmp/OutputReadyPacket.hpp"

namespace cmmp {

const PacketId OutputReadyPacket::id = PacketId::OutputReady;

OutputReadyPacket OutputReadyPacket::decode(std::istream& is) {
    std::string license = StreamUtils::read<std::string>(is);
    std::string destination = StreamUtils::read<std::string>(is);
    uint16_t capacity = StreamUtils::read<uint16_t>(is);

    return OutputReadyPacket(license, destination, capacity);
}

void OutputReadyPacket::encode(std::ostream& os) const {
    StreamUtils::write(os, license);
    StreamUtils::write(os, destination);
    StreamUtils::write(os, capacity);
}

}
