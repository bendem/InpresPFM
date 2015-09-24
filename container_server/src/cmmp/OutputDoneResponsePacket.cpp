#include "cmmp/OutputDoneResponsePacket.hpp"

OutputDoneResponsePacket OutputDoneResponsePacket::decode(const std::vector<char>& v) {
    std::vector<char>::const_iterator it = v.begin();
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
