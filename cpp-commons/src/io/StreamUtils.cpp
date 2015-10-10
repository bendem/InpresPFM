#include "io/StreamUtils.hpp"

template<>
std::string StreamUtils::read<std::string>(std::istream& is) {
    uint64_t len = StreamUtils::read<uint64_t>(is);
    std::string res(len, 0);
    is.read(&res.front(), len);
    return res;
}

template<>
std::ostream& StreamUtils::write<std::string>(std::ostream& os, std::string s) {
    StreamUtils::write<uint64_t>(os, s.size());
    os.write(&s[0], s.size());
    return os;
}
