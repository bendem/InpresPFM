#ifndef CONTAINER_SERVER_OUTPUTDONERESPONSEPACKET_HPP
#define CONTAINER_SERVER_OUTPUTDONERESPONSEPACKET_HPP

#include <string>

#include "cmmp/PacketId.hpp"
#include "protocol/Packet.hpp"

class OutputDoneResponsePacket : public Packet<OutputDoneResponsePacket> {

public:
    OutputDoneResponsePacket(bool ok, std::string reason)
        : Packet(PacketId::OutputDoneResponse),
          ok(ok),
          reason(reason) {}

    bool isOk() const { return ok; }
    const std::string& getReason() const { return reason; }

    static OutputDoneResponsePacket decode(std::vector<char>::const_iterator&);
    void encode(std::vector<char>&) const;

private:
    bool ok;
    std::string reason;

};

#endif
