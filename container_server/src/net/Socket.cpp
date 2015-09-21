#include <iostream>
#include "net/Socket.hpp"

Socket::Socket(unsigned short port) {
    struct sockaddr_in addr;
    addr.sin_family = AF_INET;
    addr.sin_port = htons(port);
    addr.sin_addr.s_addr = INADDR_ANY;

    this->setupSocket(addr);
}

Socket::Socket(unsigned short port, std::string bindHost) {
    struct hostent *host;
    struct in_addr ip;
    struct sockaddr_in addr;

    if((host = gethostbyname(bindHost.c_str())) == 0) {
        throw std::runtime_error("Failed to get host for '" + bindHost + "': " + std::to_string(errno));
    }
    memcpy(&ip, host->h_addr, host->h_length);

    memset(&addr, 0, sizeof(struct sockaddr_in));
    addr.sin_family = AF_INET;
    addr.sin_port = htons(port);
    addr.sin_addr = ip;

    this->setupSocket(addr);
}

void Socket::setupSocket(const sockaddr_in addr) {
    this->addr = *(struct sockaddr*) &addr;
    this->addrLen = sizeof(struct sockaddr_in);

    this->handle = socket(AF_INET, SOCK_STREAM, IPPROTO_IP);
    if(this->handle < 0) {
        throw std::runtime_error("Failed to create socket: " + std::to_string(errno));
    }

    if(bind(this->handle, &this->addr, this->addrLen) == -1) {
        throw std::runtime_error("Could not bind socket: " + std::to_string(errno));
    }
}

Socket::~Socket() {
    if(this->handle > 0) {
        close(this->handle);
    }
}

Socket& Socket::listen(int max_connections) {
    if((::listen(this->handle, max_connections)) < 0) {
        throw std::runtime_error("Could not start listening: " + std::to_string(errno));
    }
    return *this;
}

Socket Socket::accept() {
    Socket s;
    if((s.handle = ::accept(this->handle, &s.addr, &s.addrLen)) < 0) {
        throw std::runtime_error("Could not accept: " + std::to_string(errno));
    }
    return s;
}

long Socket::write(const std::vector<char>& vector) {
    return send(this->handle, vector.data(), vector.size() * sizeof(char), 0);
}

std::vector<char> Socket::read() {
    std::vector<char> result;
    char c;

    while(recv(this->handle, &c, 1, 0) == 1) {
        std::cout << c;
        result.push_back(c);
    }

    return result;
}
