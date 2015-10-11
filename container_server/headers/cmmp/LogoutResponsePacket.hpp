#ifndef CONTAINER_SERVER_LOGOUTRESPONSEPACKET_HPP
#define CONTAINER_SERVER_LOGOUTRESPONSEPACKET_HPP

#include <string>

#include "cmmp/PacketId.hpp"
#include "protocol/Packet.hpp"

class LogoutResponsePacket : public Packet<LogoutResponsePacket> {

public:
    static const PacketId id;

    LogoutResponsePacket(bool ok, std::string reason)
        : ok(ok),
          reason(reason) {}

    bool isOk() const { return ok; }
    const std::string& getReason() const { return reason; }

    static LogoutResponsePacket decode(std::istream&);
    void encode(std::ostream&) const;

private:
    bool ok;
    std::string reason;

};

#endif
