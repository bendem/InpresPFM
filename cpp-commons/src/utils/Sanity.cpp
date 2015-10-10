#include "utils/Sanity.hpp"

void Sanity::truthness(bool cond, const std::string& error) {
    if(!cond) {
        throw std::logic_error(error);
    }
}
