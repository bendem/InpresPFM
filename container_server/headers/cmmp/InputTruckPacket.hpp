#ifndef CONTAINER_SERVER_INPUTTRUCKPACKET_HPP
#define CONTAINER_SERVER_INPUTTRUCKPACKET_HPP

#include <string>

#include "cmmp/PacketId.hpp"
#include "protocol/Packet.hpp"

class InputTruckPacket : public Packet<InputTruckPacket> {

public:
    InputTruckPacket(const std::string& license, const std::vector<std::string>& containerIds)
        : Packet(PacketId::InputTruck),
          license(license),
          containerIds(containerIds) {}

    const std::string& getLicense() const { return license; }
    const std::vector<std::string>& getContainerIds() const { return containerIds; }

    static InputTruckPacket decode(std::vector<char>::const_iterator&);
    void encode(std::vector<char>&) const;

private:
    std::string license;
    std::vector<std::string> containerIds;

};

#endif
