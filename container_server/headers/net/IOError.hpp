#ifndef CONTAINER_SERVER_IOERROR_HPP
#define CONTAINER_SERVER_IOERROR_HPP

#include <stdexcept>

class IOError : public std::runtime_error {

public:
    IOError(const std::string& what) : runtime_error(what) { }

};

#endif //CONTAINER_SERVER_IOERROR_HPP
