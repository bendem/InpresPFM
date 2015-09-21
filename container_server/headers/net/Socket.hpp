#ifndef CONTAINER_SERVER_SOCKET_HPP
#define CONTAINER_SERVER_SOCKET_HPP

#include <cstring>
#include <string>
#include <vector>

#include <netdb.h>
#include <unistd.h>
#include <netinet/in.h>
#include <sys/socket.h>

#include "net/IOError.hpp"

class Socket {

public:
    Socket(unsigned short port);
    Socket(unsigned short port, std::string bindHost);
    ~Socket();

    Socket& listen(int);

    Socket accept();

    void close();

    long write(const std::vector<char>&);

    std::vector<char> read(unsigned int);

private:
    int handle;
    struct sockaddr addr;
    unsigned int addrLen;
    bool closed;

    Socket() {}
    void setupSocket(const struct sockaddr_in);
    void error(const std::string&);
    void checkOpen() const;

};

#endif //CONTAINER_SERVER_SOCKET_HPP
