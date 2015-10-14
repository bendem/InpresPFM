#ifndef CPP_COMMONS_STREAMUTILS_HPP
#define CPP_COMMONS_STREAMUTILS_HPP

#include <cassert>
#include <cstdint>
#include <iostream>

#include <netinet/in.h>

#include "utils/Logger.hpp"

using std::istream;
using std::ostream;
using std::string;

class StreamUtils {

public:
    template<class T>
    static T read(std::istream&) {
        static_assert(sizeof(T) == 0, "Can't use non specialized StreamUtils::read");
        assert(false);
    }

    template<class T>
    static std::ostream& write(std::ostream&, const T&) {
        static_assert(sizeof(T) == 0, "Can't use non specialized StreamUtils::write");
        assert(false);
    }

};

template<> uint8_t StreamUtils::read<uint8_t>(istream&);
template<> uint16_t StreamUtils::read<uint16_t>(istream&);
template<> uint32_t StreamUtils::read<uint32_t>(istream&);
template<> uint64_t StreamUtils::read<uint64_t>(istream&);
template<> int16_t StreamUtils::read<int16_t>(istream&);
template<> int32_t StreamUtils::read<int32_t>(istream&);
template<> int64_t StreamUtils::read<int64_t>(istream&);
template<> bool StreamUtils::read<bool>(istream&);
template<> float StreamUtils::read<float>(istream&);
template<> string StreamUtils::read<string>(istream&);

template<> std::ostream& StreamUtils::write<uint8_t>(ostream&, const uint8_t&);
template<> std::ostream& StreamUtils::write<uint16_t>(ostream&, const uint16_t&);
template<> std::ostream& StreamUtils::write<uint32_t>(ostream&, const uint32_t&);
template<> std::ostream& StreamUtils::write<uint64_t>(ostream&, const uint64_t&);
template<> std::ostream& StreamUtils::write<int16_t>(ostream&, const int16_t&);
template<> std::ostream& StreamUtils::write<int32_t>(ostream&, const int32_t&);
template<> std::ostream& StreamUtils::write<int64_t>(ostream&, const int64_t&);
template<> std::ostream& StreamUtils::write<bool>(ostream&, const bool&);
template<> std::ostream& StreamUtils::write<float>(ostream&, const float&);
template<> std::ostream& StreamUtils::write<string>(ostream&, const string&);

#endif
