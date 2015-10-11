#ifndef CONTAINER_SERVER_LOGINRESPONSEPACKET_HPP
#define CONTAINER_SERVER_LOGINRESPONSEPACKET_HPP

#include <string>

#include "cmmp/PacketId.hpp"
#include "protocol/Packet.hpp"

class LoginResponsePacket : public Packet<LoginResponsePacket> {

public:
    static const PacketId id;

    LoginResponsePacket(bool ok, std::string reason = "")
        : ok(ok),
          reason(reason) {}

    bool isOk() const { return ok; }
    const std::string& getReason() const { return reason; }

    static LoginResponsePacket decode(std::istream&);
    void encode(std::ostream&) const;

private:
    bool ok;
    std::string reason;

};

#endif
