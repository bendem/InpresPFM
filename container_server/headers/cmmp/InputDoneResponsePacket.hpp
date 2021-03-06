#ifndef CONTAINER_SERVER_INPUTDONERESPONSEPACKET_HPP
#define CONTAINER_SERVER_INPUTDONERESPONSEPACKET_HPP

#include <string>

#include "cmmp/PacketId.hpp"
#include "protocol/Packet.hpp"

namespace cmmp {

class InputDoneResponsePacket : public Packet<InputDoneResponsePacket> {

public:
    static const PacketId id;

    InputDoneResponsePacket(bool ok, std::string reason = "")
        : ok(ok),
          reason(reason) {}

    bool isOk() const { return ok; }
    const std::string& getReason() const { return reason; }

    static InputDoneResponsePacket decode(std::istream&);
    void encode(std::ostream&) const;

private:
    bool ok;
    std::string reason;

};

}

#endif
