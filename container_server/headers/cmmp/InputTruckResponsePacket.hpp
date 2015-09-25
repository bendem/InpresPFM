#ifndef CONTAINER_SERVER_INPUTTRUCKRESPONSEPACKET_HPP
#define CONTAINER_SERVER_INPUTTRUCKRESPONSEPACKET_HPP

#include <string>

#include "cmmp/PacketId.hpp"
#include "protocol/Packet.hpp"

class InputTruckResponsePacket : public Packet<InputTruckResponsePacket> {

public:
    InputTruckResponsePacket(bool ok, uint32_t x, uint32_t y, std::string reason)
        : Packet(PacketId::InputTruckResponse),
          ok(ok),
          x(x),
          y(y),
          reason(reason) {}

    bool isOk() const { return ok; }
    uint32_t getX() const { return x; }
    uint32_t getY() const { return y; }

    static InputTruckResponsePacket decode(std::vector<char>::const_iterator&);
    void encode(std::vector<char>&) const;

private:
    bool ok;
    uint32_t x;
    uint32_t y;
    std::string reason;

};

#endif //CONTAINER_SERVER_INPUTTRUCKRESPONSEPACKET_HPP
