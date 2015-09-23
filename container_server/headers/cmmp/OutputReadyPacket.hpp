#ifndef CONTAINER_SERVER_OUTPUTREADYPACKET_HPP
#define CONTAINER_SERVER_OUTPUTREADYPACKET_HPP

#include <string>

#include "cmmp/PacketId.hpp"
#include "protocol/Packet.hpp"

class OutputReadyPacket : public Packet<OutputReadyPacket> {

public:
    OutputReadyPacket(const std::string& license, const std::string& destination, uint32_t capacity)
            : Packet(PacketId::OutputReady),
              license(license),
              destination(destination),
              capacity(capacity) {}

    const std::string& getLicense() const { return license; }
    const std::string& getDestination() const { return destination; }
    uint32_t getCapacity() const { return capacity; }

    static OutputReadyPacket decode(const std::vector<char>&);
    void encode(std::vector<char>&);

private:
    std::string license;
    std::string destination;
    uint32_t capacity;

};

#endif //CONTAINER_SERVER_OUTPUTREADYPACKET_HPP
