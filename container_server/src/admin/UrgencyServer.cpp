#include "admin/UrgencyServer.hpp"

UrgencyServer::UrgencyServer(unsigned short port)
    : closed(false) {
    this->socket.bind(port);
    this->thread = std::thread(&UrgencyServer::accept, this);
}

UrgencyServer::~UrgencyServer() {
    close();
}

void UrgencyServer::accept() {
    while(!closed) {
        std::shared_ptr<Socket> accepted;
        try {
            accepted = socket.accept();
        } catch(IOError e) {
            LOG << Logger::Error << "Something bad happened: " << e.what();
            return;
        }

        accepted->registerCloseHandler([this](Socket& closed, Socket::CloseReason) {
            std::lock_guard<std::mutex> lk(this->socketsMutex);
            std::remove_if(this->sockets.begin(), this->sockets.end(), [&closed](std::shared_ptr<Socket> s) {
                return closed.getHandle() == s->getHandle();
            });
        });
        {
            std::lock_guard<std::mutex> lk(this->socketsMutex);
            this->sockets.push_back(accepted);
        }
    }
}

UrgencyServer& UrgencyServer::send(const std::string& string) {
    if(string.length() > UINT16_MAX) {
        throw std::runtime_error("Message too long");
    }

    std::ostringstream os;
    StreamUtils::write<uint16_t>(os, string.length());
    for(unsigned i = 0; i < string.length(); ++i) {
        os.put(string[i]);
    }

    std::lock_guard<std::mutex> lk(this->socketsMutex);
    for(auto s : sockets) {
        s->write(os.str());
    }
    return *this;
}

void UrgencyServer::close() {
    if(closed.exchange(true)) {
        return;
    }

    std::lock_guard<std::mutex> lk(socketsMutex);
    for(auto item : sockets) {
        item->close();
    }
    socket.close();
}
