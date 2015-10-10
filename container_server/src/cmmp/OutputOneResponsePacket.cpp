#include "cmmp/OutputOneResponsePacket.hpp"

const PacketId OutputOneResponsePacket::id = PacketId::OutputOneResponse;

OutputOneResponsePacket OutputOneResponsePacket::decode(std::vector<char>::const_iterator& it) {
    bool ok = readPrimitive<bool>(it);
    std::string reason = ok ? "" : readString(it);

    return OutputOneResponsePacket(ok, reason);
}

void OutputOneResponsePacket::encode(std::vector<char>& v) const {
    writePrimitive(v, ok);
    if(!ok) {
        writeString(v, reason);
    }
}
