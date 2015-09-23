#ifndef CONTAINER_SERVER_INPUTDONEPACKET_HPP
#define CONTAINER_SERVER_INPUTDONEPACKET_HPP

#include "cmmp/PacketId.hpp"
#include "protocol/Packet.hpp"

class InputDonePacket : public Packet<InputDonePacket> {

public:
    InputDonePacket(bool ok, float weight)
        : Packet(PacketId::InputDone),
          ok(ok),
          weight(weight) {}

    bool isOk() const { return ok; }
    float getWeight() const { return weight; }

    static InputDonePacket decode(const std::vector<char>&);
    void encode(std::vector<char>&);

private:
    bool ok;
    float weight;

};

#endif //CONTAINER_SERVER_INPUTDONEPACKET_HPP
