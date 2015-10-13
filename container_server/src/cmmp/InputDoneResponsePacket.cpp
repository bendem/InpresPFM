#include "cmmp/InputDoneResponsePacket.hpp"

const PacketId InputDoneResponsePacket::id = PacketId::InputDoneResponse;

InputDoneResponsePacket InputDoneResponsePacket::decode(std::istream& is) {
    bool ok = StreamUtils::read<bool>(is);
    std::string reason = ok ? "" : StreamUtils::read<std::string>(is);

    return InputDoneResponsePacket(ok, reason);
}

void InputDoneResponsePacket::encode(std::ostream& os) const {
    StreamUtils::write(os, ok);
    if(!ok) {
        StreamUtils::write(os, reason);
    }
}
