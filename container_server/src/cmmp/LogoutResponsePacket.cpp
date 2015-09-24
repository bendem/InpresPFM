#include "cmmp/LogoutResponsePacket.hpp"

LogoutResponsePacket LogoutResponsePacket::decode(const std::vector<char>& v) {
    std::vector<char>::const_iterator it = v.begin();
    bool ok = readPrimitive<bool>(it);
    std::string reason = ok ? "" : readString(it);

    return LogoutResponsePacket(ok, reason);
}

void LogoutResponsePacket::encode(std::vector<char>& v) const {
    writePrimitive(v, ok);
    if(!ok) {
        writeString(v, reason);
    }
}
