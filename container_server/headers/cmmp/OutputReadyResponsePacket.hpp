#ifndef CONTAINER_SERVER_OUTPUTREADYRESPONSEPACKET_HPP
#define CONTAINER_SERVER_OUTPUTREADYRESPONSEPACKET_HPP

#include <string>

#include "cmmp/PacketId.hpp"
#include "protocol/Packet.hpp"

class OutputReadyResponsePacket : public Packet<OutputReadyResponsePacket> {

public:
    OutputReadyResponsePacket(bool ok, uint32_t size, const std::vector<uint32_t>& containerIds, const std::string& reason)
            : Packet(PacketId::OutputReadyResponse),
              ok(ok),
              size(size),
              containerIds(containerIds),
              reason(reason) {}

    bool isOk() const { return ok; }
    uint32_t getSize() const { return size; }
    const std::vector<uint32_t>& getContainerIds() const { return containerIds; }
    const std::string& getReason() const { return reason; }

    static OutputReadyResponsePacket decode(const std::vector<char>&);
    void encode(std::vector<char>&) const;

private:
    bool ok;
    uint32_t size;
    std::vector<uint32_t> containerIds;
    std::string reason;

};

#endif //CONTAINER_SERVER_OUTPUTREADYRESPONSEPACKET_HPP
