#ifndef CONTAINER_SERVER_INPUTTRUCKRESPONSEPACKET_HPP
#define CONTAINER_SERVER_INPUTTRUCKRESPONSEPACKET_HPP

#include <string>

#include "cmmp/PacketId.hpp"
#include "protocol/Packet.hpp"
#include "Container.hpp"

class InputTruckResponsePacket : public Packet<InputTruckResponsePacket> {

public:
    InputTruckResponsePacket(bool ok, std::vector<Container> containers, std::string reason)
        : Packet(PacketId::InputTruckResponse),
          ok(ok),
          containers(containers),
          reason(reason) {}

    bool isOk() const { return ok; }
    std::vector<Container> getContainers() const { return containers; }

    static InputTruckResponsePacket decode(std::vector<char>::const_iterator&);
    void encode(std::vector<char>&) const;

private:
    bool ok;
    std::vector<Container> containers;
    std::string reason;

};

#endif
