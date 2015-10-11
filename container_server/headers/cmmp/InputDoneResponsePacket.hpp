#ifndef CONTAINER_SERVER_INPUTDONERESPONSEPACKET_HPP
#define CONTAINER_SERVER_INPUTDONERESPONSEPACKET_HPP

#include <string>

#include "cmmp/PacketId.hpp"
#include "protocol/Packet.hpp"

class InputDoneResponsePacket : public Packet<InputDoneResponsePacket> {

public:
    static const PacketId id;

    InputDoneResponsePacket(bool ok, std::string reason = "")
        : ok(ok),
          reason(reason) {}

    bool isOk() const { return ok; }
    const std::string& getReason() const { return reason; }

    static InputDoneResponsePacket decode(std::vector<char>::const_iterator&);
    void encode(std::vector<char>&) const;

private:
    bool ok;
    std::string reason;

};

#endif
