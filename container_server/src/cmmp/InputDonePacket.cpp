#include "cmmp/InputDonePacket.hpp"

InputDonePacket InputDonePacket::decode(const std::vector<char>& vector) {
    std::vector<char>::const_iterator it = vector.begin();
    bool ok = readPrimitive<bool>(it);
    float weight = ok ? readPrimitive<float>(it) : 0;

    return InputDonePacket(ok, weight);
}

void InputDonePacket::encode(std::vector<char>& v) {
    writePrimitive(v, ok);
    writePrimitive(v, weight);
}
