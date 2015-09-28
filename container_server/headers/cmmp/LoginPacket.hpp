#ifndef CONTAINER_SERVER_LOGINPACKET_HPP
#define CONTAINER_SERVER_LOGINPACKET_HPP

#include <string>

#include "cmmp/PacketId.hpp"
#include "protocol/Packet.hpp"

class LoginPacket : public Packet<LoginPacket> {

public:
    LoginPacket(const std::string& username, const std::string& password, bool newUser)
        : Packet(PacketId::Login),
          username(username),
          password(password),
          newUser(newUser) {}

    const std::string& getUsername() const { return username; }
    const std::string& getPassword() const { return password; }
    bool isNew() const { return newUser; }

    static LoginPacket decode(std::vector<char>::const_iterator&);
    void encode(std::vector<char>&) const;

private:
    std::string username;
    std::string password;
    bool newUser;
};

#endif
