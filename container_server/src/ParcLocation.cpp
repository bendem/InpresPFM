#include "ParcLocation.hpp"

std::ostream& operator<<(std::ostream& os, const ParcLocation& p) {
    StreamUtils::write(os, p.x);
    StreamUtils::write(os, p.y);
    StreamUtils::write(os, p.containerId);
    StreamUtils::write<uint8_t>(os, p.flag);
    StreamUtils::write(os, p.reservationDate);
    StreamUtils::write(os, p.arrivalDate);
    StreamUtils::write(os, p.weight);
    StreamUtils::write(os, p.destination);
    StreamUtils::write<uint8_t>(os, p.meanOfTranspartation);

    return os;
}

std::istream& operator>>(std::istream& is, ParcLocation& p) {
    p.x                    = StreamUtils::read<decltype(p.x)>          (is);
    p.y                    = StreamUtils::read<decltype(p.y)>          (is);
    p.containerId          = StreamUtils::read<decltype(p.containerId)>(is);
    p.flag                 = static_cast<ParkLocationFlag::ParkLocationFlag>(StreamUtils::read<uint8_t>(is));
    p.reservationDate      = StreamUtils::read<decltype(p.reservationDate)>(is);
    p.arrivalDate          = StreamUtils::read<decltype(p.arrivalDate)>(is);
    p.weight               = StreamUtils::read<decltype(p.weight)>(is);
    p.destination          = StreamUtils::read<decltype(p.destination)>(is);
    p.meanOfTranspartation = static_cast<MeanOfTransportation::MeanOfTransportation>(StreamUtils::read<uint8_t>(is));

    return is;
}
