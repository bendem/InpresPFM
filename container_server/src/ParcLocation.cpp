#include "ParcLocation.hpp"

std::ostream& operator<<(std::ostream& os, const ParcLocation& p) {
    StreamUtils::write(os, p.x);
    StreamUtils::write(os, p.y);
    StreamUtils::write(os, p.containerId);
    StreamUtils::write(os, p.flag);
    StreamUtils::write(os, p.reservationDate);
    StreamUtils::write(os, p.arrivalDate);
    StreamUtils::write(os, p.weight);
    StreamUtils::write(os, p.destination);
    StreamUtils::write(os, p.meanOfTranspartation);

    return os;
}

std::istream& operator>>(std::istream& is, ParcLocation& p) {
    p.x                    = StreamUtils::read<decltype(p.x)>          (is);
    p.y                    = StreamUtils::read<decltype(p.y)>          (is);
    p.containerId          = StreamUtils::read<decltype(p.containerId)>(is);
    p.flag                 = StreamUtils::read<decltype(p.flag)>       (is);
    p.reservationDate      = StreamUtils::read<decltype(p.reservationDate)>(is);
    p.arrivalDate          = StreamUtils::read<decltype(p.arrivalDate)>(is);
    p.weight               = StreamUtils::read<decltype(p.weight)>(is);
    p.destination          = StreamUtils::read<decltype(p.destination)>(is);
    p.meanOfTranspartation = StreamUtils::read<decltype(p.meanOfTranspartation)>(is);

    return is;
}
