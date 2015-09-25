#include "cmmp/LoginResponsePacket.hpp"

LoginResponsePacket LoginResponsePacket::decode(std::vector<char>::const_iterator& it) {
    bool ok = readPrimitive<bool>(it);
    std::string reason = ok ? "" : readString(it);

    return LoginResponsePacket(ok, reason);
}

void LoginResponsePacket::encode(std::vector<char>& v) const {
    writePrimitive(v, ok);
    if(!ok) {
        writeString(v, reason);
    }
}
