#ifndef CONTAINER_SERVER_INPUTTRUCKRESPONSEPACKET_HPP
#define CONTAINER_SERVER_INPUTTRUCKRESPONSEPACKET_HPP

#include <string>

#include "Container.hpp"
#include "cmmp/PacketId.hpp"
#include "protocol/Packet.hpp"

namespace cmmp {

class InputTruckResponsePacket : public Packet<InputTruckResponsePacket> {

public:
    static const PacketId id;

    InputTruckResponsePacket(bool ok, std::vector<Container> containers, std::string reason = "")
        : ok(ok),
          containers(containers),
          reason(reason) {}

    bool isOk() const { return ok; }
    std::vector<Container> getContainers() const { return containers; }
    std::string getReason() const { return reason; }

    static InputTruckResponsePacket decode(std::istream&);
    void encode(std::ostream&) const;

private:
    bool ok;
    std::vector<Container> containers;
    std::string reason;

};

}

#endif
