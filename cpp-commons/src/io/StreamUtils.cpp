#include "io/StreamUtils.hpp"

template<> uint8_t StreamUtils::read<uint8_t>(istream& is) {
    uint8_t i;
    is.read(reinterpret_cast<char*>(&i), sizeof(uint8_t));
    return i;
}

template<> uint16_t StreamUtils::read<uint16_t>(istream& is) {
    uint16_t i = 0;
    is.read(reinterpret_cast<char*>(&i), sizeof(uint16_t));
    return ntohs(i);
}

template<> uint32_t StreamUtils::read<uint32_t>(istream& is) {
    uint32_t i = 0;
    is.read(reinterpret_cast<char*>(&i), sizeof(uint32_t));
    return ntohl(i);
}

template<> uint64_t StreamUtils::read<uint64_t>(istream& is) {
#if __BYTE_ORDER__ == __ORDER_BIG_ENDIAN__
    uint64_t x = read<uint32_t>(is);
    uint32_t y = read<uint32_t>(is);
#else
    uint32_t y = ntohl(read<uint32_t>(is));
    uint64_t x = ntohl(read<uint32_t>(is));
#endif
    return (x << 32) | y;
}

template<> int16_t StreamUtils::read<int16_t>(istream& is) {
    return static_cast<int16_t>(read<uint16_t>(is));
}

template<> int32_t StreamUtils::read<int32_t>(istream& is) {
    return static_cast<int32_t>(read<uint32_t>(is));
}

template<> int64_t StreamUtils::read<int64_t>(istream& is) {
    return static_cast<int64_t>(read<uint64_t>(is));
}

template<> bool StreamUtils::read<bool>(istream& is) {
    return read<uint8_t>(is) == 1;
}

template<> float StreamUtils::read<float>(istream& is) {
    // Public domain implementation from
    // http://beej.us/guide/bgnet/output/html/singlepage/bgnet.html#serialization

    uint64_t i = read<uint64_t>(is);
    const unsigned bits = 32, expbits = 8;
    float result;
    int64_t shift;
    unsigned bias;
    unsigned significandbits = bits - expbits - 1; // -1 for sign bit

    if (i == 0) return 0.0f;

    // pull the significand
    result = (i & ((1LL << significandbits) - 1)); // mask
    result /= 1LL << significandbits; // convert back to float
    result += 1.0f; // add the one back on

    // deal with the exponent
    bias = (1 << (expbits - 1)) - 1;
    shift = (i >> significandbits & ((1LL << expbits) - 1)) - bias;
    while(shift > 0) {
        result *= 2.0; shift--;
    }
    while(shift < 0) {
        result /= 2.0; shift++;
    }

    // sign it
    result *= i >> (bits - 1) & 1 ? -1.0 : 1.0;

    return result;
}

template<> string StreamUtils::read<string>(istream& is) {
    uint32_t len = read<uint32_t>(is);
    string res(len, 0);
    is.read(&res.front(), len);
    return res;
}

template<> ostream& StreamUtils::write<uint8_t>(ostream& os, const uint8_t& i) {
    os.write(reinterpret_cast<const char*>(&i), sizeof(uint8_t));
    return os;
}

template<> ostream& StreamUtils::write<uint16_t>(ostream& os, const uint16_t& i) {
    uint16_t x = htons(i);
    os.write(reinterpret_cast<char*>(&x), sizeof(uint16_t));
    return os;
}

template<> ostream& StreamUtils::write<uint32_t>(ostream& os, const uint32_t& i) {
    uint32_t x = htonl(i);
    os.write(reinterpret_cast<char*>(&x), sizeof(uint32_t));
    return os;
}

template<> ostream& StreamUtils::write<uint64_t>(ostream& os, const uint64_t& i) {
#if __BYTE_ORDER__ == __ORDER_BIG_ENDIAN__
    write(os, static_cast<uint32_t>(i >> 32));
    write(os, static_cast<uint32_t>(i & UINT32_MAX));
#else
    write(os, htonl(static_cast<uint32_t>(i &  UINT32_MAX)));
    write(os, htonl(static_cast<uint32_t>(i >> 32)));
#endif
    return os;
}

template<> ostream& StreamUtils::write<int16_t>(ostream& os, const int16_t& i) {
    return write(os, static_cast<const uint16_t&>(i));
}

template<> ostream& StreamUtils::write<int32_t>(ostream& os, const int32_t& i) {
    return write(os, static_cast<const uint32_t&>(i));
}

template<> ostream& StreamUtils::write<int64_t>(ostream& os, const int64_t& i) {
    return write(os, static_cast<const uint64_t&>(i));
}

template<> ostream& StreamUtils::write<bool>(ostream& os, const bool& b) {
    return write<uint8_t>(os, b ? 1 : 0);
}

template<> ostream& StreamUtils::write<float>(ostream& os, const float& f) {
    // Public domain implementation from
    // http://beej.us/guide/bgnet/output/html/singlepage/bgnet.html#serialization

    if (f == 0.0) {
        // get this special case out of the way
        return write<int64_t>(os, 0);
    }

    const unsigned bits = 32, expbits = 8;
    const unsigned significandbits = bits - expbits - 1; // -1 for sign bit
    long double fnorm;
    int shift;
    int64_t sign, exp, significand;

    // check sign and begin normalization
    if (f < 0) {
        sign = 1; fnorm = -f;
    } else {
        sign = 0; fnorm = f;
    }

    // get the normalized form of f and track the exponent
    shift = 0;
    while(fnorm >= 2.0) {
        fnorm /= 2.0; shift++;
    }
    while(fnorm < 1.0) {
        fnorm *= 2.0; shift--;
    }
    fnorm = fnorm - 1.0;

    // calculate the binary form (non-float) of the significand data
    significand = fnorm * ((1LL << significandbits) + 0.5f);

    // get the biased exponent
    exp = shift + ((1<<(expbits-1)) - 1); // shift + bias

    // return the final answer
    return write<int64_t>(os, (sign << (bits-1)) | (exp << (bits - expbits - 1)) | significand);
}

template<> ostream& StreamUtils::write<string>(ostream& os, const string& s) {
    write<uint32_t>(os, s.size());
    os.write(&s[0], s.size());
    return os;
}
