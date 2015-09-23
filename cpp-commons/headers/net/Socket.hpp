#ifndef CONTAINER_SERVER_SOCKET_HPP
#define CONTAINER_SERVER_SOCKET_HPP

#include <array>
#include <cstring>
#include <string>
#include <vector>

#include <netdb.h>
#include <unistd.h>
#include <netinet/in.h>
#include <sys/socket.h>

#include "net/IOError.hpp"

// TODO Move bind to method (not used for clients)
// TODO connect method for clients
class Socket {

public:
    Socket() : handle(-1), closed(false) {}
    ~Socket();

    Socket& connect(unsigned short, std::string);

    Socket& bind(unsigned short);
    Socket& bind(unsigned short, std::string);

    Socket accept();

    void close();

    long write(const std::vector<char>&);

    std::vector<char> read(unsigned int);
    void accumulate(unsigned int, std::vector<char>&);

private:
    int handle;
    struct sockaddr addr;
    unsigned int addrLen;
    bool closed;

    struct sockaddr_in setupPort(unsigned short);
    struct sockaddr_in setupHostAndPort(unsigned short, std::string);
    Socket& setupSocket(const struct sockaddr_in, bool bind);
    void error(const std::string&);
    void checkOpen() const;

};

#endif //CONTAINER_SERVER_SOCKET_HPP
