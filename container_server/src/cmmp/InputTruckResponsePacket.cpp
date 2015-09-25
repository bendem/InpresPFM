#include "cmmp/InputTruckResponsePacket.hpp"

InputTruckResponsePacket InputTruckResponsePacket::decode(std::vector<char>::const_iterator& it) {
    bool ok = readPrimitive<bool>(it);
    std::string reason;
    uint32_t x;
    uint32_t y;
    if (ok) {
        reason = "";
        x = readPrimitive<uint32_t>(it);
        y = readPrimitive<uint32_t>(it);
    } else {
        reason = readString(it);
        x = 0;
        y = 0;
    }

    return InputTruckResponsePacket(ok, x, y, reason);
}

void InputTruckResponsePacket::encode(std::vector<char>& v) const {
    writePrimitive(v, ok);
    if(!ok) {
        writeString(v, reason);
    } else {
        writePrimitive(v, x);
        writePrimitive(v, y);
    }
}
