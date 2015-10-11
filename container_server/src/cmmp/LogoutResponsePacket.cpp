#include "cmmp/LogoutResponsePacket.hpp"

const PacketId LogoutResponsePacket::id = PacketId::LogoutResponse;

LogoutResponsePacket LogoutResponsePacket::decode(std::istream& is) {
    bool ok = StreamUtils::read<bool>(is) ;
    std::string reason = ok ? "" : StreamUtils::read<std::string>(is) ;

    return LogoutResponsePacket(ok, reason);
}

void LogoutResponsePacket::encode(std::ostream& os) const {
    StreamUtils::write(os, ok);
    if(!ok) {
        StreamUtils::write(os, reason);
    }
}
