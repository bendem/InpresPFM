#include "cmmp/OutputReadyResponsePacket.hpp"

const PacketId OutputReadyResponsePacket::id = PacketId::OutputReadyResponse;

OutputReadyResponsePacket OutputReadyResponsePacket::decode(std::istream& is) {
    bool ok = StreamUtils::read<bool>(is) ;
    std::string reason = ok ? "" : StreamUtils::read<std::string>(is) ;
    uint32_t size = ok ? StreamUtils::read<uint32_t>(is)  : 0;
    std::vector<std::string> containerIds;
    if (size) {
        for(uint32_t i = 0; i < size; i++) {
            containerIds.push_back(StreamUtils::read<std::string>(is) );
        }
    }

    return OutputReadyResponsePacket(ok, containerIds, reason);
}

void OutputReadyResponsePacket::encode(std::ostream& os) const {
    StreamUtils::write(os, ok);
    if(!ok) {
        StreamUtils::write(os, reason);
    } else {
        StreamUtils::write<uint32_t>(os,  containerIds.size());
        for(const std::string& value : containerIds) {
            StreamUtils::write(os, value);
        }
    }
}
