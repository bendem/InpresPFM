#include "cmmp/LoginResponsePacket.hpp"

const PacketId LoginResponsePacket::id = PacketId::LoginResponse;

LoginResponsePacket LoginResponsePacket::decode(std::istream& is) {
    bool ok = StreamUtils::read<bool>(is);
    std::string reason = ok ? "" : StreamUtils::read<std::string>(is);

    return LoginResponsePacket(ok, reason);
}

void LoginResponsePacket::encode(std::ostream& os) const {
    StreamUtils::write(os, ok);
    if(!ok) {
        StreamUtils::write(os, reason);
    }
}
