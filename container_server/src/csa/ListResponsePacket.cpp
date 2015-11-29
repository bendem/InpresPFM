#include "csa/ListResponsePacket.hpp"

namespace csa {

ListResponsePacket ListResponsePacket::decode(std::istream& is) {
    StreamUtils::read<uint8_t>(is); // First byte is a null check java side
    uint32_t size = StreamUtils::read<uint32_t>(is);
    std::vector<std::string> ips;
    ips.reserve(size);
    for(uint32_t i = 0; i < size; ++i) {
        ips.push_back(StreamUtils::read<std::string>(is));
    }
    return ListResponsePacket(ips);
}

void ListResponsePacket::encode(ostream& os) const {
    StreamUtils::write<uint8_t>(os, 0); // First byte is a null check java side
    StreamUtils::write<uint32_t>(os, ips.size());

    for(const std::string& ip : ips) {
        StreamUtils::write<std::string>(os, ip);
    }
}

}
