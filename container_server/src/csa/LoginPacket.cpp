#include "csa/LoginPacket.hpp"

namespace csa {

LoginPacket LoginPacket::decode(std::istream& is) {
    std::string username = StreamUtils::read<std::string>(is);
    std::string password = StreamUtils::read<std::string>(is);
    return LoginPacket(username, password);
}

void LoginPacket::encode(std::ostream& os) const {
    StreamUtils::write(os, username);
    StreamUtils::write(os, password);
}

}
