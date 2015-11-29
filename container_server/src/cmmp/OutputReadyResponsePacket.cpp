#include "cmmp/OutputReadyResponsePacket.hpp"

namespace cmmp {

const PacketId OutputReadyResponsePacket::id = PacketId::OutputReadyResponse;

OutputReadyResponsePacket OutputReadyResponsePacket::decode(std::istream& is) {
    bool ok = StreamUtils::read<bool>(is);
    uint32_t size;
    std::vector<Container> containers;
    std::string reason;

    if(ok) {
        size = StreamUtils::read<uint32_t>(is);
        for(uint32_t i = 0; i < size; ++i) {
            containers.push_back(StreamUtils::read<Container>(is));
        }
    } else {
        reason = StreamUtils::read<std::string>(is);
    }


    return OutputReadyResponsePacket(ok, containers, reason);
}

void OutputReadyResponsePacket::encode(std::ostream& os) const {
    StreamUtils::write(os, ok);
    if(!ok) {
        StreamUtils::write(os, reason);
    } else {
        StreamUtils::write<uint32_t>(os,  containers.size());
        for(const Container& value : containers) {
            StreamUtils::write(os, value);
        }
    }
}

}
