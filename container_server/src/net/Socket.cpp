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
        this->error("Failed to get host for '" + bindHost + "': " + std::to_string(errno));
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
    this->closed = false;

    this->handle = socket(AF_INET, SOCK_STREAM, IPPROTO_IP);
    if(this->handle < 0) {
        this->error("Failed to create socket: " + std::to_string(errno));
    }

    if(bind(this->handle, &this->addr, this->addrLen) == -1) {
        this->error("Could not bind socket: " + std::to_string(errno));
    }
}

Socket::~Socket() {
    if(!this->closed) {
        ::close(this->handle);
    }
}

Socket& Socket::listen(int max_connections) {
    this->checkOpen();

    if((::listen(this->handle, max_connections)) < 0) {
        this->error("Could not start listening: " + std::to_string(errno));
    }
    return *this;
}

void Socket::close() {
    ::close(this->handle);
    this->closed = true;
}

Socket Socket::accept() {
    this->checkOpen();

    Socket s;
    if((s.handle = ::accept(this->handle, &s.addr, &s.addrLen)) < 0) {
        this->error("Could not accept: " + std::to_string(errno));
    }
    s.closed = false;

    return s;
}

long Socket::write(const std::vector<char>& vector) {
    this->checkOpen();

    long len = send(this->handle, vector.data(), vector.size() * sizeof(char), 0);

    if(len == 0) {
        this->closed = true;
    } else if(len == -1) {
        this->error("Failed to write " + std::to_string(vector.size()) + " bytes");
    }

    return len;
}

std::vector<char> Socket::read(unsigned int max) {
    this->checkOpen();

    std::vector<char> result;
    char c[max];
    long len = recv(this->handle, &c, max, 0);

    if(len == 0) {
        this->closed = true;
    } else if(len == -1) {
        this->error("Failed to read " + std::to_string(max) + " bytes");
    }

    result.reserve(len);
    for(long i = 0; i < len; ++i) {
        result.push_back(c[i]);
    }

    return result;
}

void Socket::error(const std::string& string) {
    this->closed = true;
    ::close(this->handle);
    throw IOError(string);
}

void Socket::checkOpen() const {
    if(this->closed) {
        throw IOError("Socket already closed");
    }
}
