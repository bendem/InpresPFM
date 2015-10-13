#include "cmmp/OutputOneResponsePacket.hpp"

const PacketId OutputOneResponsePacket::id = PacketId::OutputOneResponse;

OutputOneResponsePacket OutputOneResponsePacket::decode(std::istream& is) {
    bool ok = StreamUtils::read<bool>(is);
    std::string reason = ok ? "" : StreamUtils::read<std::string>(is);

    return OutputOneResponsePacket(ok, reason);
}

void OutputOneResponsePacket::encode(std::ostream& os) const {
    StreamUtils::write(os, ok);
    if(!ok) {
        StreamUtils::write(os, reason);
    }
}
