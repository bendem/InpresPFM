#include "cmmp/InputDoneResponsePacket.hpp"

const PacketId InputDoneResponsePacket::id = PacketId::InputDoneResponse;

InputDoneResponsePacket InputDoneResponsePacket::decode(std::vector<char>::const_iterator& it) {
    bool ok = readPrimitive<bool>(it);
    std::string reason = ok ? "" : readString(it);

    return InputDoneResponsePacket(ok, reason);
}

void InputDoneResponsePacket::encode(std::vector<char>& v) const {
    writePrimitive(v, ok);
    if(!ok) {
        writeString(v, reason);
    }
}
