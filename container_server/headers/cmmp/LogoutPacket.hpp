#ifndef CONTAINER_SERVER_LOGOUTPACKET_HPP
#define CONTAINER_SERVER_LOGOUTPACKET_HPP

#include <string>

class LogoutPacket {

public:
    LogoutPacket(const std::string& username, const std::string& password)
            : username(username), password(password) {}

    const std::string& getUsername() const { return username; }
    const std::string& getPassword() const { return password; }

private:
    std::string username;
    std::string password;

};

#endif //CONTAINER_SERVER_LOGOUTPACKET_HPP
