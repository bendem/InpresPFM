#include "cmmp/OutputDonePacket.hpp"

const PacketId OutputDonePacket::id = PacketId::OutputDone;

OutputDonePacket OutputDonePacket::decode(std::vector<char>::const_iterator& it) {
    std::string license = readString(it);
    uint16_t container_count = readPrimitive<uint16_t>(it);

    return OutputDonePacket(license, container_count);
}

void OutputDonePacket::encode(std::vector<char>& v) const {
    writeString(v, license);
    writePrimitive(v, containerCount);
}
