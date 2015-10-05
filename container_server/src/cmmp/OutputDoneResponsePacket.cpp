#include "cmmp/OutputDoneResponsePacket.hpp"

const PacketId OutputDoneResponsePacket::id = PacketId::OutputDoneResponse;

OutputDoneResponsePacket OutputDoneResponsePacket::decode(std::vector<char>::const_iterator& it) {
    bool ok = readPrimitive<bool>(it);
    std::string reason = ok ? "" : readString(it);

    return OutputDoneResponsePacket(ok, reason);
}

void OutputDoneResponsePacket::encode(std::vector<char>& v) const {
    writePrimitive(v, ok);
    if(!ok) {
        writeString(v, reason);
    }
}
