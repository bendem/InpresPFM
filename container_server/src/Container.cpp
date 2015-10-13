#include "Container.hpp"

template<>
std::ostream& StreamUtils::write<Container>(std::ostream& os, Container container) {
    StreamUtils::write(os, container.id);
    StreamUtils::write(os, container.destination);
    StreamUtils::write(os, container.x);
    StreamUtils::write(os, container.y);
    return os;
}

template<> Container StreamUtils::read<Container>(std::istream& is) {
    Container c;

    c.id          = StreamUtils::read<decltype(c.id)>(is);
    c.destination = StreamUtils::read<decltype(c.destination)>(is);
    c.x           = StreamUtils::read<decltype(c.x)>(is);
    c.y           = StreamUtils::read<decltype(c.y)>(is);

    return c;
}
