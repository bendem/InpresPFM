#include "cmmp/InputTruckPacket.hpp"

InputTruckPacket InputTruckPacket::decode(std::vector<char>::const_iterator& it) {
    std::string license = readString(it);
    uint32_t size = readPrimitive<uint32_t>(it);
    std::vector<std::pair<std::string, std::string>> containers;
    if (size) {
        std::string id;
        std::string destination;
        for(uint32_t i = 0; i < size; i++) {
            id = readString(it);
            destination = readString(it);
            containers.push_back(std::make_pair(id, destination));
        }
    }

    return InputTruckPacket(license, containers);
}

void InputTruckPacket::encode(std::vector<char>& v) const {
    writeString(v, license);
    writePrimitive<uint32_t>(v, containers.size());
    for(auto value : containers) {
        writeString(v, std::get<0>(value));
        writeString(v, std::get<1>(value));
    }
}
