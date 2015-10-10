#include "ParcLocation.hpp"

std::ostream& operator<<(std::ostream& os, const ParcLocation& p) {
    StreamUtils::write(os, p.x);
    StreamUtils::write(os, p.y);
    StreamUtils::write(os, p.containerId);
    StreamUtils::write(os, p.flag);

    return os;
}

std::istream& operator>>(std::istream& is, ParcLocation& p) {
    p.x           = StreamUtils::read<decltype(p.x)>          (is);
    p.y           = StreamUtils::read<decltype(p.y)>          (is);
    p.containerId = StreamUtils::read<decltype(p.containerId)>(is);
    p.flag        = StreamUtils::read<decltype(p.flag)>       (is);

    return is;
}
