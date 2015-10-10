#ifndef CONTAINER_SERVER_PARKLOCATION_HPP
#define CONTAINER_SERVER_PARKLOCATION_HPP

#include <cstdint>
#include <string>

#include "io/StreamUtils.hpp"

namespace ParkLocationFlag {
    enum ParkLocationFlag : uint8_t {
        Free, Reserved, Taken
    };
}

struct ParkLocation {
    uint16_t         x;
    uint16_t         y;
    std::string      containerId;
    ParkLocationFlag::ParkLocationFlag flag;
};

std::ostream& operator<<(std::ostream& os, const ParkLocation& p);
std::istream& operator>>(std::istream& is, ParkLocation& p);

#endif
