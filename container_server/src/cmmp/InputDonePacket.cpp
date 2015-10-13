#include "cmmp/InputDonePacket.hpp"

const PacketId InputDonePacket::id = PacketId::InputDone;

InputDonePacket InputDonePacket::decode(std::istream& is) {
    bool ok = StreamUtils::read<bool>(is);
    float weight = ok ? StreamUtils::read<float>(is)  : 0;

    return InputDonePacket(ok, weight);
}

void InputDonePacket::encode(std::ostream& os) const {
    StreamUtils::write(os, ok);
    if(ok) {
        StreamUtils::write(os, weight);
    }
}
