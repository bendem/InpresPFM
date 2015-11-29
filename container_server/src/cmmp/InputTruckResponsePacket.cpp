#include "cmmp/InputTruckResponsePacket.hpp"

namespace cmmp {

const PacketId InputTruckResponsePacket::id = PacketId::InputTruckResponse;

InputTruckResponsePacket InputTruckResponsePacket::decode(std::istream& is) {
    bool ok = StreamUtils::read<bool>(is);
    std::string reason;
    std::vector<Container> containers;

    if (ok) {
        uint32_t size = StreamUtils::read<uint32_t>(is);
        for(uint32_t i = 0; i < size; ++i) {
            containers.emplace_back(StreamUtils::read<Container>(is));
        }
    } else {
        reason = StreamUtils::read<std::string>(is);
    }

    return InputTruckResponsePacket(ok, containers, reason);
}

void InputTruckResponsePacket::encode(std::ostream& os) const {
    StreamUtils::write(os, ok);
    if(ok) {
        StreamUtils::write<uint32_t>(os,  containers.size());
        for(const Container& container : containers) {
            StreamUtils::write(os, container);
        }
    } else {
        StreamUtils::write(os, reason);
    }
}

}
