#include "cmmp/OutputOnePacket.hpp"


OutputOnePacket OutputOnePacket::decode(const std::vector<char>& v) {
    std::vector<char>::const_iterator it = v.begin();
    std::string container_id = readString(it);

    return OutputOnePacket(container_id);
}

void OutputOnePacket::encode(std::vector<char>& v) {
    writeString(v, containerId);
}
