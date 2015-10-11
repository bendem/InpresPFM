#ifndef CONTAINER_SERVER_OUTPUTDONERESPONSEPACKET_HPP
#define CONTAINER_SERVER_OUTPUTDONERESPONSEPACKET_HPP

#include <string>

#include "cmmp/PacketId.hpp"
#include "protocol/Packet.hpp"

class OutputDoneResponsePacket : public Packet<OutputDoneResponsePacket> {

public:
    static const PacketId id;

    OutputDoneResponsePacket(bool ok, std::string reason = "")
        : ok(ok),
          reason(reason) {}

    bool isOk() const { return ok; }
    const std::string& getReason() const { return reason; }

    static OutputDoneResponsePacket decode(std::istream&);
    void encode(std::ostream&) const;

private:
    bool ok;
    std::string reason;

};

#endif
