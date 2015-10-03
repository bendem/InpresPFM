#include "utils/Sanity.hpp"

void Sanity::truthness(bool cond, const std::string& error) {
    if(!cond) {
        throw std::logic_error(error);
    }
}

void Sanity::streamness(const std::istream& s, const std::string& error) {
    if(s.fail()) {
        throw std::runtime_error(error);
    }
}

void Sanity::streamness(const std::ostream& s, const std::string& error) {
    if(s.fail()) {
        throw std::runtime_error(error);
    }
}

template<class T>
void Sanity::nullness(const T& ptr, const std::string& error) {
    truthness(ptr != nullptr, error);
}
