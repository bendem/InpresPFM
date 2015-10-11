#include "cmmp/LogoutPacket.hpp"

const PacketId LogoutPacket::id = PacketId::Logout;

LogoutPacket LogoutPacket::decode(std::istream& is) {
    std::string username = StreamUtils::read<std::string>(is) ;
    std::string password = StreamUtils::read<std::string>(is) ;

    return LogoutPacket(username, password);
}

void LogoutPacket::encode(std::ostream& os) const {
    StreamUtils::write(os, this->username);
    StreamUtils::write(os, this->password);
}
