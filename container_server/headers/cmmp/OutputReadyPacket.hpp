#ifndef CONTAINER_SERVER_OUTPUTREADYPACKET_HPP
#define CONTAINER_SERVER_OUTPUTREADYPACKET_HPP

#include <string>

#include "cmmp/PacketId.hpp"
#include "protocol/Packet.hpp"

namespace cmmp {

class OutputReadyPacket : public Packet<OutputReadyPacket> {

public:
    static const PacketId id;

    OutputReadyPacket(const std::string& license, const std::string& destination, uint16_t capacity)
        : license(license),
          destination(destination),
          capacity(capacity) {}

    const std::string& getLicense() const { return license; }
    const std::string& getDestination() const { return destination; }
    uint16_t getCapacity() const { return capacity; }

    static OutputReadyPacket decode(std::istream&);
    void encode(std::ostream&) const;

private:
    std::string license;
    std::string destination;
    uint16_t capacity;

};

}

#endif
