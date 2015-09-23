#ifndef CONTAINER_SERVER_OUTPUTONEPACKET_HPP
#define CONTAINER_SERVER_OUTPUTONEPACKET_HPP

#include <string>

#include "cmmp/PacketId.hpp"
#include "protocol/Packet.hpp"

class OutputOnePacket : public Packet<OutputOnePacket> {

public:
    OutputOnePacket(const std::string& container_id)
        : Packet(PacketId::OutputOne),
          containerId(container_id) {}

    const std::string& getContainerId() const { return containerId; }

    static OutputOnePacket decode(const std::vector<char>&);
    void encode(std::vector<char>&);

private:
    std::string containerId;

};

#endif //CONTAINER_SERVER_OUTPUTONEPACKET_HPP
