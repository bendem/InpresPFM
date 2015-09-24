#ifndef CONTAINER_SERVER_PROTOCOLERROR_HPP
#define CONTAINER_SERVER_PROTOCOLERROR_HPP

#include <stdexcept>

class ProtocolError : public std::runtime_error {

public:
    ProtocolError(const std::string& what) : runtime_error(what) {}

};

#endif //CONTAINER_SERVER_PROTOCOLERROR_HPP
