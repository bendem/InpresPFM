#ifndef CONTAINER_SERVER_CONTAINER_HPP
#define CONTAINER_SERVER_CONTAINER_HPP

#include <string>

#include "cmmp/PacketId.hpp"
#include "protocol/Packet.hpp"

class Container {

public:
    Container(std::string id, std::string destination, std::pair<uint32_t, uint32_t> position)
            : id(id),
              destination(destination),
              position(position) {}

    std::string getId() const { return id; }
    std::string getDestination() const { return destination; }
    std::pair<uint32_t, uint32_t> getPosition() const { return position; }
    uint32_t getX() const { return std::get<0>(position); }
    uint32_t getY() const { return std::get<1>(position); }

private:
    std::string id;
    std::string destination;
    std::pair<uint32_t, uint32_t> position;

};

#endif
