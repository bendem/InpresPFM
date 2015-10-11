#ifndef CONTAINER_SERVER_OUTPUTREADYRESPONSEPACKET_HPP
#define CONTAINER_SERVER_OUTPUTREADYRESPONSEPACKET_HPP

#include <string>

#include "cmmp/PacketId.hpp"
#include "protocol/Packet.hpp"

class OutputReadyResponsePacket : public Packet<OutputReadyResponsePacket> {

public:
    static const PacketId id;

    OutputReadyResponsePacket(bool ok, const std::vector<std::string>& containerIds, const std::string& reason = "")
        : ok(ok),
          containerIds(containerIds),
          reason(reason) {}

    bool isOk() const { return ok; }
    const std::vector<std::string>& getContainerIds() const { return containerIds; }
    const std::string& getReason() const { return reason; }

    static OutputReadyResponsePacket decode(std::istream&);
    void encode(std::ostream&) const;

private:
    bool ok;
    std::vector<std::string> containerIds;
    std::string reason;

};

#endif
