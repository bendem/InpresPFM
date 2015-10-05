#include "cmmp/InputTruckPacket.hpp"

const PacketId InputTruckPacket::id = PacketId::InputTruck;

InputTruckPacket InputTruckPacket::decode(std::vector<char>::const_iterator& it) {
    std::string license = readString(it);
    uint32_t size = readPrimitive<uint32_t>(it);
    std::vector<Container> containers;
    if (size) {
        std::string id;
        std::string destination;
        for(uint32_t i = 0; i < size; i++) {
            id = readString(it);
            destination = readString(it);
            containers.emplace_back(Container { id, destination, 0, 0 });
        }
    }

    return InputTruckPacket(license, containers);
}

void InputTruckPacket::encode(std::vector<char>& v) const {
    writeString(v, license);
    writePrimitive<uint32_t>(v, containers.size());
    for(const Container& container : containers) {
        writeString(v, container.id);
        writeString(v, container.destination);
    }
}
