#include "cmmp/LoginPacket.hpp"

namespace cmmp {

const PacketId LoginPacket::id = PacketId::Login;

LoginPacket LoginPacket::decode(std::istream& is) {
    std::string username = StreamUtils::read<std::string>(is);
    std::string password = StreamUtils::read<std::string>(is);
    bool newUser = StreamUtils::read<bool>(is);

    return LoginPacket(username, password, newUser);
}

void LoginPacket::encode(std::ostream& os) const {
    StreamUtils::write(os, this->username);
    StreamUtils::write(os, this->password);
    StreamUtils::write(os, this->newUser);
}

}
