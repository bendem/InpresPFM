#ifndef CONTAINER_SERVER_SOCKET_HPP
#define CONTAINER_SERVER_SOCKET_HPP

#include <stdexcept>
#include <string>
#include <vector>

#include <cstring>
#include <netdb.h>
#include <unistd.h>
#include <netinet/in.h>
#include <sys/socket.h>

class Socket {

public:
    Socket(unsigned short port);
    Socket(unsigned short port, std::string bindHost);
    ~Socket();

    Socket& listen(int);

    Socket accept();

    long write(const std::vector<char>&vector);

    std::vector<char> read();

private:
    int handle;
    struct sockaddr addr;
    unsigned int addrLen;

    Socket() {}
    void setupSocket(const struct sockaddr_in);

};

#endif //CONTAINER_SERVER_SOCKET_HPP
