#include "cmmp/OutputOnePacket.hpp"

OutputOnePacket OutputOnePacket::decode(std::vector<char>::const_iterator& it) {
    std::string container_id = readString(it);

    return OutputOnePacket(container_id);
}

void OutputOnePacket::encode(std::vector<char>& v) const {
    writeString(v, containerId);
}
