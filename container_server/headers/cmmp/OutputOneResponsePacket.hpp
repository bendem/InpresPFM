#ifndef CONTAINER_SERVER_OUTPUTONERESPONSERESPONSEPACKET_HPP
#define CONTAINER_SERVER_OUTPUTONERESPONSERESPONSEPACKET_HPP

#include <string>

#include "cmmp/PacketId.hpp"
#include "protocol/Packet.hpp"

namespace cmmp {

class OutputOneResponsePacket : public Packet<OutputOneResponsePacket> {

public:
    static const PacketId id;

    OutputOneResponsePacket(bool ok, std::string reason = "")
        : ok(ok),
          reason(reason) {}

    bool isOk() const { return ok; }
    const std::string& getReason() const { return reason; }

    static OutputOneResponsePacket decode(std::istream&);
    void encode(std::ostream&) const;

private:
    bool ok;
    std::string reason;

};

}

#endif
