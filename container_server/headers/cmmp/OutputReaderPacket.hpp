#ifndef CONTAINER_SERVER_OUTPUTREADERPACKET_HPP
#define CONTAINER_SERVER_OUTPUTREADERPACKET_HPP

#include <string>

class OutputReadyPacket {

public:
    OutputReadyPacket(const std::string& license, const std::string& destination, uint32_t capacity)
            : license(license), destination(destination), capacity(capacity) {}

    const std::string& getLicense() const { return license; }
    const std::string& getDestination() const { return destination; }
    uint32_t getCapacity() const { return capacity; }

private:
    std::string license;
    std::string destination;
    uint32_t capacity;

};

#endif //CONTAINER_SERVER_OUTPUTREADERPACKET_HPP
