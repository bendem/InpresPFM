#ifndef CONTAINER_SERVER_PARKLOCATION_HPP
#define CONTAINER_SERVER_PARKLOCATION_HPP

#include <cstdint>
#include <string>

#include "io/StreamUtils.hpp"

namespace ParkLocationFlag {
    enum ParkLocationFlag : uint8_t {
        Free,     // Place can be taken by anyone
        Reserved, // Place can only be taken if the containerId matches
        Storing,  // Place is being filled (should not be stored in the file)
        Leaving,  // Place is being freed (should not be stored in the file)
        Taken     // Place is taken
    };
}

namespace MeanOfTransportation {
    enum MeanOfTransportation : uint8_t {
        Boat, Train
    };
}

struct ParcLocation {
    uint16_t             x;
    uint16_t             y;
    std::string          containerId;
    ParkLocationFlag::ParkLocationFlag flag;
    std::string          reservationDate;
    std::string          arrivalDate;
    uint32_t             weight;
    std::string          destination;
    MeanOfTransportation::MeanOfTransportation meanOfTranspartation;
};

// Allow to be used by BinaryFile
std::ostream& operator<<(std::ostream& os, const ParcLocation& p);
std::istream& operator>>(std::istream& is, ParcLocation& p);

#endif
