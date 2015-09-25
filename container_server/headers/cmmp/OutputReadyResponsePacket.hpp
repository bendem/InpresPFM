#ifndef CONTAINER_SERVER_OUTPUTREADYRESPONSEPACKET_HPP
#define CONTAINER_SERVER_OUTPUTREADYRESPONSEPACKET_HPP

#include <string>

#include "cmmp/PacketId.hpp"
#include "protocol/Packet.hpp"

class OutputReadyResponsePacket : public Packet<OutputReadyResponsePacket> {

public:
    OutputReadyResponsePacket(bool ok, const std::vector<std::string>& containerIds, const std::string& reason)
            : Packet(PacketId::OutputReadyResponse),
              ok(ok),
              containerIds(containerIds),
              reason(reason) {}

    bool isOk() const { return ok; }
    uint32_t getSize() const { return containerIds.size(); }
    const std::vector<std::string>& getContainerIds() const { return containerIds; }
    const std::string& getReason() const { return reason; }

    static OutputReadyResponsePacket decode(std::vector<char>::const_iterator&);
    void encode(std::vector<char>&) const;

private:
    bool ok;
    std::vector<std::string> containerIds;
    std::string reason;

};

#endif //CONTAINER_SERVER_OUTPUTREADYRESPONSEPACKET_HPP
