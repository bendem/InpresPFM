#include "cmmp/LoginPacket.hpp"

LoginPacket LoginPacket::decode(std::vector<char>::const_iterator& it) {
    std::string username = readString(it);
    std::string password = readString(it);

    return LoginPacket(username, password);
}

void LoginPacket::encode(std::vector<char>& vector) const {
    writeString(vector, this->username);
    writeString(vector, this->password);
}
