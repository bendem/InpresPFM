#include "cmmp/InputTruckPacket.hpp"

InputTruckPacket InputTruckPacket::decode(const std::vector<char>& v) {
    std::vector<char>::const_iterator it = v.begin();
    std::string license = readString(it);
    std::string container_id = readString(it);

    return InputTruckPacket(license, container_id);
}

void InputTruckPacket::encode(std::vector<char>& v) const {
    writeString(v, license);
    writeString(v, containerId);
}
