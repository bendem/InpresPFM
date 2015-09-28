#ifndef CONTAINER_SERVER_LOGINRESPONSEPACKET_HPP
#define CONTAINER_SERVER_LOGINRESPONSEPACKET_HPP

#include <string>

#include "cmmp/PacketId.hpp"
#include "protocol/Packet.hpp"

class LoginResponsePacket : public Packet<LoginResponsePacket> {

public:
    LoginResponsePacket(bool ok, std::string reason)
        : Packet(PacketId::LoginResponse),
          ok(ok),
          reason(reason) {}

    bool isOk() const { return ok; }
    const std::string& getReason() const { return reason; }

    static LoginResponsePacket decode(std::vector<char>::const_iterator&);
    void encode(std::vector<char>&) const;

private:
    bool ok;
    std::string reason;

};

#endif
