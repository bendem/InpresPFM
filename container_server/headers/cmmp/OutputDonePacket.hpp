#ifndef CONTAINER_SERVER_OUTPUTDONEPACKET_HPP
#define CONTAINER_SERVER_OUTPUTDONEPACKET_HPP

#include <string>

#include "cmmp/PacketId.hpp"
#include "protocol/Packet.hpp"

namespace cmmp {

class OutputDonePacket : public Packet<OutputDonePacket> {

public:
    static const PacketId id;

    OutputDonePacket(const std::string& license, uint16_t container_count)
        : license(license),
          containerCount(container_count) {}

    const std::string& getLicense() const { return license; }
    uint16_t getContainerCount() const { return containerCount; }

    static OutputDonePacket decode(std::istream&);
    void encode(std::ostream&) const;

private:
    std::string license;
    uint16_t containerCount;

};

}

#endif
