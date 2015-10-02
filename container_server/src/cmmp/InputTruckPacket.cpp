#include "cmmp/InputTruckPacket.hpp"

InputTruckPacket InputTruckPacket::decode(std::vector<char>::const_iterator& it) {
    std::string license = readString(it);
    uint32_t size = readPrimitive<uint32_t>(it);
    std::vector<std::string> containerIds;
    if (size) {
        for(uint32_t i = 0; i < size; i++) {
            containerIds.push_back(readString(it));
        }
    }

    return InputTruckPacket(license, containerIds);
}

void InputTruckPacket::encode(std::vector<char>& v) const {
    writeString(v, license);
    writePrimitive<uint32_t>(v, containerIds.size());
    for(auto value : containerIds) {
        writeString(v, value);
    }
}
