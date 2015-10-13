#include "cmmp/OutputDoneResponsePacket.hpp"

const PacketId OutputDoneResponsePacket::id = PacketId::OutputDoneResponse;

OutputDoneResponsePacket OutputDoneResponsePacket::decode(std::istream& is) {
    bool ok = StreamUtils::read<bool>(is);
    std::string reason = ok ? "" : StreamUtils::read<std::string>(is);

    return OutputDoneResponsePacket(ok, reason);
}

void OutputDoneResponsePacket::encode(std::ostream& os) const {
    StreamUtils::write(os, ok);
    if(!ok) {
        StreamUtils::write(os, reason);
    }
}
