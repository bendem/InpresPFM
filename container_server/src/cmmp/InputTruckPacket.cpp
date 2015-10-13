#include "cmmp/InputTruckPacket.hpp"

const PacketId InputTruckPacket::id = PacketId::InputTruck;

InputTruckPacket InputTruckPacket::decode(std::istream& is) {
    std::string license = StreamUtils::read<std::string>(is);
    uint32_t size = StreamUtils::read<uint32_t>(is);

    std::vector<Container> containers;
    for(uint32_t i = 0; i < size; i++) {
        containers.emplace_back(StreamUtils::read<Container>(is));
    }

    return InputTruckPacket(license, containers);
}

void InputTruckPacket::encode(std::ostream& os) const {
    StreamUtils::write(os, license);
    StreamUtils::write<uint32_t>(os,  containers.size());
    for(const Container& container : containers) {
        StreamUtils::write(os, container);
    }
}
