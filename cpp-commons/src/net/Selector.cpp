#include "net/Selector.hpp"

Selector::Selector() {}

Selector& Selector::addSocket(Socket& socket) {
    this->sockets.emplace(
        socket.getHandle(),
        std::weak_ptr<Socket>(std::shared_ptr<Socket>(&socket))
    );
    return *this;
}

Selector& Selector::removeSocket(Socket& socket) {
    this->sockets.erase(socket.getHandle());
    return *this;
}

std::vector<std::weak_ptr<Socket>> Selector::select() {
    fd_set set;
    FD_ZERO(&set);
    for(auto item : this->sockets) {
        FD_SET(item.first, &set);
    }
    int retval = ::select(this->sockets.size(), &set, nullptr, nullptr, nullptr);

    if(retval == 0) {
        // Retrying
        return this->select();
    }

    if(retval < 0) {
        throw IOError(std::string("Failed on select: ") + strerror(errno));
    }

    std::vector<std::weak_ptr<Socket>> sockets;
    for(auto item : this->sockets) {
        if(FD_ISSET(item.first, &set)) {
            sockets.push_back(item.second);
        }
    }

    return sockets;
}
