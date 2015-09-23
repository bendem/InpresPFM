#ifndef CONTAINER_SERVER_LOGOUTPACKET_HPP
#define CONTAINER_SERVER_LOGOUTPACKET_HPP

#include <string>

#include "cmmp/PacketId.hpp"
#include "protocol/Packet.hpp"

class LogoutPacket : public Packet<LogoutPacket> {

public:
    LogoutPacket(const std::string& username, const std::string& password)
        : Packet(PacketId::Logout),
          username(username),
          password(password) {}

    const std::string& getUsername() const { return username; }
    const std::string& getPassword() const { return password; }

    static LogoutPacket decode(const std::vector<char>&);
    void encode(std::vector<char>&);

private:
    std::string username;
    std::string password;

};

#endif //CONTAINER_SERVER_LOGOUTPACKET_HPP
