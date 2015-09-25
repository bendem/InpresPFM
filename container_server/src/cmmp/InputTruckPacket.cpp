#include "cmmp/InputTruckPacket.hpp"

InputTruckPacket InputTruckPacket::decode(std::vector<char>::const_iterator& it) {
    std::string license = readString(it);
    std::string container_id = readString(it);

    return InputTruckPacket(license, container_id);
}

void InputTruckPacket::encode(std::vector<char>& v) const {
    writeString(v, license);
    writeString(v, containerId);
}
