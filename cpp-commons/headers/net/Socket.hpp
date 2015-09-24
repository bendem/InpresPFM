#ifndef CONTAINER_SERVER_SOCKET_HPP
#define CONTAINER_SERVER_SOCKET_HPP

#include <iostream>

#include <array>
#include <cstring>
#include <memory>
#include <string>
#include <vector>

#include <netdb.h>
#include <unistd.h>
#include <netinet/in.h>
#include <sys/socket.h>

#include "net/IOError.hpp"

class Socket {

public:
    Socket()
        : handle(new int, [](int* handle) {
            std::cout << "closing " << *handle << std::endl;
            if(*handle >= 0) ::close(*handle);
            delete handle;
        }) {}

    Socket& connect(unsigned short, std::string);

    Socket& bind(unsigned short);
    Socket& bind(unsigned short, std::string);

    Socket accept();

    long write(const std::vector<char>&);

    std::vector<char> read(unsigned int);
    void accumulate(unsigned int, std::vector<char>&);

    int getHandle() const { return *handle; }

private:
    std::shared_ptr<int> handle;
    struct sockaddr addr;
    unsigned int addrLen;

    struct sockaddr_in setupPort(unsigned short);
    struct sockaddr_in setupHostAndPort(unsigned short, std::string);
    Socket& setupSocket(const struct sockaddr_in, bool bind);
    void error(const std::string&, int);
    void checkOpen() const;

};

#endif //CONTAINER_SERVER_SOCKET_HPP
