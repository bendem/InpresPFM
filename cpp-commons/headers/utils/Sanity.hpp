#ifndef CPP_COMMONS_SANITY_HPP
#define CPP_COMMONS_SANITY_HPP

#include <iostream>
#include <stdexcept>
#include <string>

class Sanity {

public:
    static void truthness(bool, const std::string&);
    static void streamness(const std::istream&, const std::string&);
    static void streamness(const std::ostream&, const std::string&);
    template<class T>
    static void nullness(const T&, const std::string&);

};

#endif
