#include "io/StreamUtils.hpp"

template<>
std::string StreamUtils::read<std::string>(std::istream& is) {
    uint32_t len = StreamUtils::read<uint32_t>(is);
    std::string res(len, 0);
    is.read(&res.front(), len);
    return res;
}

template<>
std::ostream& StreamUtils::write<std::string>(std::ostream& os, std::string s) {
    StreamUtils::write<uint32_t>(os, s.size());
    LOG << Logger::Debug << "Writing str: " << s;
    os.write(&s[0], s.size());
    return os;
}
