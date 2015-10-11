#ifndef CONTAINER_SERVER_INPUTDONEPACKET_HPP
#define CONTAINER_SERVER_INPUTDONEPACKET_HPP

#include "cmmp/PacketId.hpp"
#include "protocol/Packet.hpp"

class InputDonePacket : public Packet<InputDonePacket> {

public:
    static const PacketId id;

    InputDonePacket(bool ok, float weight)
        : ok(ok),
          weight(weight) {}

    bool isOk() const { return ok; }
    float getWeight() const { return weight; }

    static InputDonePacket decode(std::istream&);
    void encode(std::ostream&) const;

private:
    bool ok;
    float weight;

};

#endif
