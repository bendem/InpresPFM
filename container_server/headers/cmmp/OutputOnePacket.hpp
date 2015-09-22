#ifndef CONTAINER_SERVER_OUTPUTONEPACKET_HPP
#define CONTAINER_SERVER_OUTPUTONEPACKET_HPP

#include <string>

class OutputOnePacket {

public:
    OutputOnePacket(const std::string& container_id)
            : containerId(container_id) {}

    const std::string& getContainerId() const { return containerId; }

private:
    std::string containerId;

};

#endif //CONTAINER_SERVER_OUTPUTONEPACKET_HPP
