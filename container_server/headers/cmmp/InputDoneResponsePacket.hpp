#ifndef CONTAINER_SERVER_INPUTDONERESPONSEPACKET_HPP
#define CONTAINER_SERVER_INPUTDONERESPONSEPACKET_HPP

#include <string>

#include "cmmp/PacketId.hpp"
#include "protocol/Packet.hpp"

class InputDoneResponsePacket : public Packet<InputDoneResponsePacket> {

public:
    InputDoneResponsePacket(bool ok, std::string reason)
        : Packet(PacketId::InputDoneResponse),
          ok(ok),
          reason(reason) {}

    bool isOk() const { return ok; }
    const std::string& getReason() const { return reason; }

    static InputDoneResponsePacket decode(const std::vector<char>&);
    void encode(std::vector<char>&) const;

private:
    bool ok;
    std::string reason;

};

#endif //CONTAINER_SERVER_INPUTDONERESPONSEPACKET_HPP
