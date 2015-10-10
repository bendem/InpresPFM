#ifndef CPP_COMMONS_STREAMUTILS_HPP
#define CPP_COMMONS_STREAMUTILS_HPP

#include <cstdint>
#include <iostream>

#include "utils/Logger.hpp"

class StreamUtils {

public:
    template<class T>
    static T read(std::istream&);

    template<class T>
    static std::ostream& write(std::ostream&, T);

};

template<>
std::string StreamUtils::read<std::string>(std::istream&);

template<>
std::ostream& StreamUtils::write<std::string>(std::ostream&, std::string);

template<class T>
T StreamUtils::read(std::istream& is) {
    T t;
    is.read(reinterpret_cast<char*>(&t), sizeof(T));
    return t;
}

template<class T>
std::ostream& StreamUtils::write(std::ostream& os, T t) {
    os.write(reinterpret_cast<char*>(&t), sizeof(T));
    return os;
}

#endif
