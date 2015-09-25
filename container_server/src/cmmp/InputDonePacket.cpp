#include "cmmp/InputDonePacket.hpp"

InputDonePacket InputDonePacket::decode(std::vector<char>::const_iterator& it) {
    bool ok = readPrimitive<bool>(it);
    float weight = ok ? readPrimitive<float>(it) : 0;

    return InputDonePacket(ok, weight);
}

void InputDonePacket::encode(std::vector<char>& v) const {
    writePrimitive(v, ok);
    if(ok) {
        writePrimitive(v, weight);
    }
}
