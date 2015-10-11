#ifndef CONTAINER_SERVER_INPUTTRUCKPACKET_HPP
#define CONTAINER_SERVER_INPUTTRUCKPACKET_HPP

#include <string>

#include "Container.hpp"
#include "cmmp/PacketId.hpp"
#include "protocol/Packet.hpp"

class InputTruckPacket : public Packet<InputTruckPacket> {

public:
    static const PacketId id;

    InputTruckPacket(const std::string& license, const std::vector<Container>& containers)
        : license(license),
          containers(containers) {}

    const std::string& getLicense() const { return license; }
    const std::vector<Container>& getContainers() const { return containers; }

    static InputTruckPacket decode(std::istream&);
    void encode(std::ostream&) const;

private:
    std::string license;
    std::vector<Container> containers;

};

#endif
