#ifndef CONTAINER_SERVER_OUTPUTDONEPACKET_HPP
#define CONTAINER_SERVER_OUTPUTDONEPACKET_HPP

#include <string>

#include "cmmp/PacketId.hpp"
#include "protocol/Packet.hpp"

class OutputDonePacket : public Packet<OutputDonePacket> {

public:
    OutputDonePacket(const std::string& license, uint32_t container_count)
        : Packet(PacketId::OutputDone),
          license(license), containerCount(container_count) {}

    const std::string& getLicense() const { return license; }
    uint32_t getContainerCount() const { return containerCount; }

    static OutputDonePacket decode(const std::vector<char>&);
    void encode(std::vector<char>&) const;

private:
    std::string license;
    uint32_t containerCount;

};

#endif //CONTAINER_SERVER_OUTPUTDONEPACKET_HPP
