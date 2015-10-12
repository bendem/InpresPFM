#ifndef CPP_COMMONS_IOERROR_HPP
#define CPP_COMMONS_IOERROR_HPP

#include <stdexcept>

class IOError : public std::runtime_error {

public:
    IOError(const std::string& what, bool reset = false) : runtime_error(what), reset(reset) { }

    const bool reset;

};

#endif
