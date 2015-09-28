#include "net/Selector.hpp"

Selector::Selector() {
    pipe(this->pipes.data());

    LOG << Logger::Debug << "Setup selector control pipe on " << this->pipes[0] << ":" << this->pipes[1];
}

Selector::~Selector() {
    close(this->pipes[0]);
    close(this->pipes[1]);
}

Selector& Selector::addSocket(Socket socket) {
    std::lock_guard<std::mutex> lock(this->socketsMutex);
    this->sockets.emplace(socket.getHandle(), socket);
    this->interrupt();
    return *this;
}

Selector& Selector::removeSocket(const Socket& socket) {
    std::lock_guard<std::mutex> lock(this->socketsMutex);
    this->sockets.erase(socket.getHandle());
    this->interrupt();
    return *this;
}

std::vector<Socket> Selector::select() {
    fd_set set;
    FD_ZERO(&set);

    int max = this->pipes[0];
    FD_SET(this->pipes[0], &set);

    {
        std::lock_guard<std::mutex> lock(this->socketsMutex);
        for(auto item : this->sockets) {
            if(item.first > max) {
                max = item.first;
            }
            FD_SET(item.first, &set);
        }
    }

    int retval = ::select(max + 1, &set, nullptr, nullptr, nullptr);

    if(retval == 0) {
        LOG << Logger::Warning << "select returned 0 somehow";
        return {};
    }

    if(retval < 0) {
        if(errno == EINTR) {
            return {};
        }
        throw IOError(std::string("Failed on select: ") + strerror(errno));
    }

    if(FD_ISSET(this->pipes[0], &set)) {
        char c[2];
        read(this->pipes[0], &c, 1);
    }

    std::vector<Socket> sockets;
    {
        std::lock_guard<std::mutex> lock(this->socketsMutex);
        std::unordered_map<int, Socket> copy(this->sockets);
        for(auto item : copy) {
            if(FD_ISSET(item.first, &set)) {
                sockets.emplace_back(item.second);
                this->sockets.erase(item.first);
            }
        }
    }

    return sockets;
}

void Selector::interrupt() const {
    LOG << Logger::Debug << "Interrupting selector";
    if(write(this->pipes[1], "", 1) < 1) {
        LOG << Logger::Error << "Failed to interrupt selector " << strerror(errno);
    }
}
