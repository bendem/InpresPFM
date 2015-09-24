#include "cmmp/InputDoneResponsePacket.hpp"

InputDoneResponsePacket InputDoneResponsePacket::decode(const std::vector<char>& vector) {
    std::vector<char>::const_iterator it = vector.begin();
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
