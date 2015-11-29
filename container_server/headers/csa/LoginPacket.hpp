#ifndef CONTAINER_SERVER_LOGINPACKET_HPP
#define CONTAINER_SERVER_LOGINPACKET_HPP

#include "PacketId.hpp"
#include "protocol/Packet.hpp"

namespace csa {

class LoginPacket : public Packet<LoginPacket> {

public:
    static const PacketId id = PacketId::Login;

    LoginPacket(const std::string& username, const std::string& password)
        : username(username),
          password(password) { }

    const std::string& getUsername() const { return username; }
    const std::string& getPassword() const { return password; }

    static LoginPacket decode(std::istream&);
    void encode(std::ostream&) const;

private:
    std::string username;
    std::string password;

};

}

#endif
