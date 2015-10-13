#ifndef CONTAINER_SERVER_OUTPUTREADYRESPONSEPACKET_HPP
#define CONTAINER_SERVER_OUTPUTREADYRESPONSEPACKET_HPP

#include <string>

#include "Container.hpp"
#include "cmmp/PacketId.hpp"
#include "protocol/Packet.hpp"

class OutputReadyResponsePacket : public Packet<OutputReadyResponsePacket> {

public:
    static const PacketId id;

    OutputReadyResponsePacket(bool ok, const std::vector<Container>& containers, const std::string& reason = "")
        : ok(ok),
          containers(containers),
          reason(reason) {}

    bool isOk() const { return ok; }
    const std::vector<Container>& getContainers() const { return containers; }
    const std::string& getReason() const { return reason; }

    static OutputReadyResponsePacket decode(std::istream&);
    void encode(std::ostream&) const;

private:
    bool ok;
    std::vector<Container> containers;
    std::string reason;

};

#endif
