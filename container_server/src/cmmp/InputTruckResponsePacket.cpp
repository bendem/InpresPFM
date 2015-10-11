#include "cmmp/InputTruckResponsePacket.hpp"

const PacketId InputTruckResponsePacket::id = PacketId::InputTruckResponse;

InputTruckResponsePacket InputTruckResponsePacket::decode(std::istream& is) {
    bool ok = StreamUtils::read<bool>(is) ;
    std::string reason;
    std::vector<Container> containers;

    if (ok) {
        uint32_t size = StreamUtils::read<uint32_t>(is) ;
        if (size) {
            std::string container_id;
            std::string destination;
            uint16_t x;
            uint16_t y;

            for(uint32_t i = 0; i < size; i++) {
                container_id = StreamUtils::read<std::string>(is) ;
                destination = StreamUtils::read<std::string>(is) ;
                x = StreamUtils::read<uint16_t>(is) ;
                y = StreamUtils::read<uint16_t>(is) ;

                containers.emplace_back(Container { container_id, destination, x, y });
            }
        }
    } else {
        reason = StreamUtils::read<std::string>(is) ;
    }

    return InputTruckResponsePacket(ok, containers, reason);
}

void InputTruckResponsePacket::encode(std::ostream& os) const {
    StreamUtils::write(os, ok);
    if(!ok) {
        StreamUtils::write(os, reason);
    } else {
        StreamUtils::write<uint32_t>(os,  containers.size());
        for(const Container& container : containers) {
            StreamUtils::write(os, container.id);
            StreamUtils::write(os, container.destination);
            StreamUtils::write(os, container.x);
            StreamUtils::write(os, container.y);
        }
    }
}
