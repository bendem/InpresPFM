#include "cmmp/InputTruckResponsePacket.hpp"

InputTruckResponsePacket InputTruckResponsePacket::decode(const std::vector<char>& v) {
    std::vector<char>::const_iterator it = v.begin();
    bool ok = readPrimitive<bool>(it);
    std::string reason = ok ? "" : readString(it);
    uint32_t x = ok ? readPrimitive<uint32_t>(it) : 0;
    uint32_t y = ok ? readPrimitive<uint32_t>(it) : 0;

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
