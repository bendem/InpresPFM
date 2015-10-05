#include "cmmp/LoginPacket.hpp"

const PacketId LoginPacket::id = PacketId::Login;

LoginPacket LoginPacket::decode(std::vector<char>::const_iterator& it) {
    std::string username = readString(it);
    std::string password = readString(it);
    bool newUser = readPrimitive<bool>(it);

    return LoginPacket(username, password, newUser);
}

void LoginPacket::encode(std::vector<char>& vector) const {
    writeString(vector, this->username);
    writeString(vector, this->password);
    writePrimitive(vector, this->newUser);
}
