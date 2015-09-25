#ifndef CONTAINER_SERVER_INPUTTRUCKPACKET_HPP
#define CONTAINER_SERVER_INPUTTRUCKPACKET_HPP

#include <string>

#include "cmmp/PacketId.hpp"
#include "protocol/Packet.hpp"

class InputTruckPacket : public Packet<InputTruckPacket> {

public:
    InputTruckPacket(const std::string& license, const std::string& container_id)
        : Packet(PacketId::InputTruck),
          license(license),
          containerId(container_id) {}

    const std::string& getLicense() const { return license; }
    const std::string& getContainerId() const {  return containerId;  }

    static InputTruckPacket decode(std::vector<char>::const_iterator&);
    void encode(std::vector<char>&) const;

private:
    std::string license;
    std::string containerId;

};

#endif //CONTAINER_SERVER_INPUTTRUCKPACKET_HPP
