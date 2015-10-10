#ifndef CPP_COMMONS_SANITY_HPP
#define CPP_COMMONS_SANITY_HPP

#include <iostream>
#include <stdexcept>
#include <string>

class Sanity {

public:
    static void truthness(bool, const std::string&);
template<class T>
static void streamness(const std::basic_ios<T>&, const std::string&);
    template<class T>
    static void nullness(const T&, const std::string&);

};

template<class T>
void Sanity::streamness(const std::basic_ios<T>& s, const std::string& error) {
    if(s.fail()) {
        throw std::runtime_error(error);
    }
}

template<class T>
void Sanity::nullness(const T& ptr, const std::string& error) {
    truthness(ptr != nullptr, error);
}

#endif
