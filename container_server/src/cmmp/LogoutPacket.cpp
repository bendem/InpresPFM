#include "cmmp/LogoutPacket.hpp"

LogoutPacket LogoutPacket::decode(const std::vector<char>& v) {
    std::vector<char>::const_iterator it = v.begin();
    std::string username = readString(it);
    std::string password = readString(it);

    return LogoutPacket(username, password);
}

void LogoutPacket::encode(std::vector<char>& vector) {
    writeString(vector, this->username);
    writeString(vector, this->password);
}
