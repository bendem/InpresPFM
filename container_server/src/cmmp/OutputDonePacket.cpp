#include "cmmp/OutputDonePacket.hpp"

namespace cmmp {

const PacketId OutputDonePacket::id = PacketId::OutputDone;

OutputDonePacket OutputDonePacket::decode(std::istream& is) {
    std::string license = StreamUtils::read<std::string>(is);
    uint16_t container_count = StreamUtils::read<uint16_t>(is);

    return OutputDonePacket(license, container_count);
}

void OutputDonePacket::encode(std::ostream& os) const {
    StreamUtils::write(os, license);
    StreamUtils::write(os, containerCount);
}

}
