#ifndef CONTAINER_SERVER_LOGOUTRESPONSEPACKET_HPP
#define CONTAINER_SERVER_LOGOUTRESPONSEPACKET_HPP

#include <string>

#include "cmmp/PacketId.hpp"
#include "protocol/Packet.hpp"

class LogoutResponsePacket : public Packet<LogoutResponsePacket> {

public:
    LogoutResponsePacket(bool ok, std::string reason)
        : Packet(PacketId::LogoutResponse),
          ok(ok),
          reason(reason) {}

    bool isOk() const { return ok; }
    const std::string& getReason() const { return reason; }

    static LogoutResponsePacket decode(std::vector<char>::const_iterator&);
    void encode(std::vector<char>&) const;

private:
    bool ok;
    std::string reason;

};

#endif //CONTAINER_SERVER_LOGINRESPONSEPACKET_HPP
