#include "cmmp/OutputDonePacket.hpp"

OutputDonePacket OutputDonePacket::decode(std::vector<char>::const_iterator& it) {
    std::string license = readString(it);
    uint32_t container_count = readPrimitive<uint32_t>(it);

    return OutputDonePacket(license, container_count);
}

void OutputDonePacket::encode(std::vector<char>& v) const {
    writeString(v, license);
    writePrimitive(v, containerCount);
}
