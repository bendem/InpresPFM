#include "cmmp/LoginPacket.hpp"

LoginPacket LoginPacket::decode(const std::vector<char>& v) {
    std::vector<char>::const_iterator it = v.begin();
    std::string username = readString(it);
    std::string password = readString(it);

    return LoginPacket(username, password);
}

void LoginPacket::encode(std::vector<char>& vector) {
    writeString(vector, this->username);
    writeString(vector, this->password);
}
