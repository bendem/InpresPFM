#include "net/Socket.hpp"

Socket::~Socket() {
    if(this->handle > 0) {
        this->close();
    }
}

Socket& Socket::connect(unsigned short port, std::string host) {
    if(this->server) {
        throw std::logic_error("Trying to connect from a server socket");
    }
    return this->setupSocket(this->setupHostAndPort(port, host), false);
}

Socket& Socket::bind(unsigned short port) {
    this->server = true;
    return this->setupSocket(this->setupPort(port), true);
}

Socket& Socket::bind(unsigned short port, std::string bindHost) {
    this->server = true;
    return this->setupSocket(this->setupHostAndPort(port, bindHost), true);
}

struct sockaddr_in Socket::setupPort(unsigned short port) {
    struct sockaddr_in addr;
    addr.sin_family = AF_INET;
    addr.sin_port = htons(port);
    addr.sin_addr.s_addr = INADDR_ANY;
    return addr;
}

struct sockaddr_in Socket::setupHostAndPort(unsigned short port, std::string host) {
    struct hostent *host_info;
    struct in_addr ip;
    struct sockaddr_in addr;

    if((host_info = gethostbyname(host.c_str())) == 0) {
        this->error("Failed to get host for '" + host + "'", errno);
    }
    memcpy(&ip, host_info->h_addr, host_info->h_length);

    memset(&addr, 0, sizeof(struct sockaddr_in));
    addr.sin_family = AF_INET;
    addr.sin_port = htons(port);
    addr.sin_addr = ip;
    return addr;
}

Socket& Socket::setupSocket(const sockaddr_in addr, bool bind) {
    this->addr = *(struct sockaddr*) &addr;
    this->addrLen = sizeof(struct sockaddr_in);

    this->handle = socket(AF_INET, SOCK_STREAM, IPPROTO_IP);
    if(this->handle < 0) {
        this->error("Failed to create socket", errno);
    }

    int yes = 1;
    if(setsockopt(this->handle, SOL_SOCKET, SO_REUSEADDR, &yes, sizeof(yes)) == -1) {
        this->error("Failed to set socket option", errno);
    }

    if(bind) {
        if(::bind(this->handle, &this->addr, this->addrLen) == -1) {
            this->error("Could not bind socket", errno);
        }
    } else {
        if(::connect(this->handle, &this->addr, this->addrLen) == -1) {
            this->error("Could not connect socket", errno);
        }
    }

    return *this;
}

std::shared_ptr<Socket> Socket::accept() {
    this->checkOpen();
    if(!this->server) {
        throw std::logic_error("Trying to accept from a non server socket");
    }

    std::lock_guard<std::recursive_mutex> lk(this->handleMutex);

    if((::listen(this->handle, SOMAXCONN)) < 0) {
        this->error("Could not start listening", errno);
    }

    std::shared_ptr<Socket> s(new Socket);

    if((s->handle = ::accept(this->handle, &s->addr, &s->addrLen)) < 0) {
        //if(errno == EINTR) {
        //    // Got interrupted
        //    LOG << Logger::Debug << "Got interrupted on accept";
        //    return this->accept();
        //}
        this->error("Could not accept", errno);
    }

    return s;
}

long Socket::write(const std::vector<char>& vector) {
    this->checkOpen();
    if(this->server) {
        throw std::logic_error("Trying to write on a server socket");
    }

    long len = send(this->handle, vector.data(), vector.size() * sizeof(char), 0);

    if(len == -1) {
        this->error("Failed to write " + std::to_string(vector.size()) + " bytes", errno);
    } else if(len == 0) {
        this->close(Gone);
        throw IOError("Failed to write, client closed the connection");
    } else if(len != vector.size()) {
        this->error("Didn't write enough bytes, "
            "expected: " + std::to_string(vector.size())
            + ", wrote " + std::to_string(len), errno);
    }

    return len;
}

std::vector<char> Socket::read(unsigned int max) {
    this->checkOpen();
    if(this->server) {
        throw std::logic_error("Trying to read on a server socket");
    }

    std::vector<char> result;
    char* c = new char[max]; // TODO Don't do that

    this->handleMutex.lock();
    ssize_t len = recv(this->handle, c, max, 0);
    this->handleMutex.unlock();

    if(len < 0) {
        delete c;
        this->error("Failed to read " + std::to_string(max) + " bytes", errno);
    } else if(len == 0) {
        this->close(Gone);
        throw IOError("Failed to read, client closed the connection");
    }

    result.reserve(len);
    for(long i = 0; i < len; ++i) {
        result.push_back(c[i]);
    }

    delete c;

    return result;
}

void Socket::accumulate(unsigned int len, std::vector<char>& result) {
    this->checkOpen();

    std::lock_guard<std::recursive_mutex> lk(this->handleMutex);

    while(result.size() < len) {
        const std::vector<char>& x = this->read(len - result.size());
        result.insert(result.end(), x.begin(), x.end());
    }
}

void Socket::error(const std::string& string, int err) {
    this->close(Error);
    throw IOError(string + ": " + strerror(err));
}

void Socket::checkOpen() const {
    if(this->isClosed()) {
        throw IOError("Socket already closed");
    }
}

void Socket::close() {
    this->close(CloseReason::Closed);
}

void Socket::close(Socket::CloseReason reason) {
    this->checkOpen();

    LOG << "closing socket " << this->handle;
    std::lock_guard<std::recursive_mutex> lk(this->handleMutex);
    ::close(this->handle);
    for(CloseHandler handler : this->closeHandlers) {
        handler(*this, reason);
    }
    this->handle = -1;
}

Socket& Socket::registerCloseHandler(Socket::CloseHandler handler) {
    std::lock_guard<std::recursive_mutex> lock(this->handleMutex);
    this->closeHandlers.emplace_back(handler);
    return *this;
}
