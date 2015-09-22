#ifndef CONTAINER_SERVER_INPUTTRUCKPACKET_HPP
#define CONTAINER_SERVER_INPUTTRUCKPACKET_HPP

#include <string>

class InputTruckPacket {

public:
    InputTruckPacket(const std::string& license, const std::string& container_id)
            : license(license), containerId(container_id) {}

    const std::string& getLicense() const { return license; }
    const std::string& getContainerId() const {  return containerId;  }

private:
    std::string license;
    std::string containerId;

};

#endif //CONTAINER_SERVER_INPUTTRUCKPACKET_HPP
