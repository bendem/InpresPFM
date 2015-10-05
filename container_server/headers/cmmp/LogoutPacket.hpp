#ifndef CONTAINER_SERVER_LOGOUTPACKET_HPP
#define CONTAINER_SERVER_LOGOUTPACKET_HPP

#include <string>

#include "cmmp/PacketId.hpp"
#include "protocol/Packet.hpp"

class LogoutPacket : public Packet<LogoutPacket> {

public:
    static const PacketId id;

    LogoutPacket(const std::string& username, const std::string& password)
        : username(username),
          password(password) {}

    const std::string& getUsername() const { return username; }
    const std::string& getPassword() const { return password; }

    static LogoutPacket decode(std::vector<char>::const_iterator&);
    void encode(std::vector<char>&) const;

private:
    std::string username;
    std::string password;

};

#endif
