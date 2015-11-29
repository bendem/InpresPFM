#ifndef CONTAINER_SERVER_OUTPUTONEPACKET_HPP
#define CONTAINER_SERVER_OUTPUTONEPACKET_HPP

#include <string>

#include "cmmp/PacketId.hpp"
#include "protocol/Packet.hpp"

namespace cmmp {

class OutputOnePacket : public Packet<OutputOnePacket> {

public:
    static const PacketId id;

    OutputOnePacket(const std::string& container_id)
        : containerId(container_id) {}

    const std::string& getContainerId() const { return containerId; }

    static OutputOnePacket decode(std::istream&);
    void encode(std::ostream&) const;

private:
    std::string containerId;

};

}

#endif
