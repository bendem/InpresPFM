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

struct ParcLocation {
    uint16_t         x;
    uint16_t         y;
    std::string      containerId;
    ParkLocationFlag::ParkLocationFlag flag;
};

// Allow to be used by BinaryFile
std::ostream& operator<<(std::ostream& os, const ParcLocation& p);
std::istream& operator>>(std::istream& is, ParcLocation& p);

#endif
