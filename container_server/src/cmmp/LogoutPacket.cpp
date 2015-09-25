#include "cmmp/LogoutPacket.hpp"

LogoutPacket LogoutPacket::decode(std::vector<char>::const_iterator& it) {
    std::string username = readString(it);
    std::string password = readString(it);

    return LogoutPacket(username, password);
}

void LogoutPacket::encode(std::vector<char>& vector) const {
    writeString(vector, this->username);
    writeString(vector, this->password);
}
