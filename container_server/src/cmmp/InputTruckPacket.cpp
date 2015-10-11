#include "cmmp/InputTruckPacket.hpp"

const PacketId InputTruckPacket::id = PacketId::InputTruck;

InputTruckPacket InputTruckPacket::decode(std::istream& is) {
    std::string license = StreamUtils::read<std::string>(is) ;
    uint32_t size = StreamUtils::read<uint32_t>(is) ;
    std::vector<Container> containers;
    if (size) {
        std::string id;
        std::string destination;
        for(uint32_t i = 0; i < size; i++) {
            id = StreamUtils::read<std::string>(is) ;
            destination = StreamUtils::read<std::string>(is) ;
            containers.emplace_back(Container { id, destination, 0, 0 });
        }
    }

    return InputTruckPacket(license, containers);
}

void InputTruckPacket::encode(std::ostream& os) const {
    StreamUtils::write(os, license);
    StreamUtils::write<uint32_t>(os,  containers.size());
    for(const Container& container : containers) {
        StreamUtils::write(os, container.id);
        StreamUtils::write(os, container.destination);
    }
}
