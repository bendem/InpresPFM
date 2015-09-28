#ifndef CONTAINER_SERVER_SOCKET_HPP
#define CONTAINER_SERVER_SOCKET_HPP

#include <array>
#include <atomic>
#include <cstring>
#include <string>
#include <vector>

#include <netdb.h>
#include <unistd.h>
#include <netinet/in.h>
#include <sys/socket.h>

#include "net/IOError.hpp"
#include "utils/Logger.hpp"

class Socket {

public:
    Socket() : handle(std::make_shared<int>(-1)) {}
    ~Socket();

    Socket& connect(unsigned short, std::string);

    Socket& bind(unsigned short);
    Socket& bind(unsigned short, std::string);

    Socket accept();

    long write(const std::vector<char>&);

    std::vector<char> read(unsigned int);
    void accumulate(unsigned int, std::vector<char>&);

    int getHandle() const { return *this->handle; }

    void close();
    bool isClosed() const { return *this->handle < 0; }

    bool operator==(const Socket& o) const { return *this->handle == *o.handle; }

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

namespace std {
    template<>
    struct hash<Socket> {
        size_t operator()(const Socket& s) const {
            return s.getHandle();
        }
    };
}

#endif //CONTAINER_SERVER_SOCKET_HPP
