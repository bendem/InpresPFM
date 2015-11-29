#include "admin/Admin.hpp"

Admin::Admin(ContainerServer& server, unsigned short port)
    : server(server),
      closed(false) {
    LOG << "Binding admin socket to " << port;
    socket.bind(port);
    thread = std::thread(&Admin::run, this);

    ListPacket::registerHandler([this](const ListPacket&, std::shared_ptr<Socket> s) {
        proto.write(s, ListResponsePacket(this->server.getConnectedIps()));
    });
    PausePacket::registerHandler([this](const PausePacket&, std::shared_ptr<Socket> s) {
        //server.togglePause();
        proto.write(s, PauseResponsePacket(""));
    });
    StopPacket::registerHandler([this](const StopPacket&, std::shared_ptr<Socket> s) {
        proto.write(s, StopResponsePacket(""));
        //server.close();
    });
}

void Admin::run() {
    LOG << Logger::Debug << "Admin running";
    while(!closed) {
        std::shared_ptr<Socket> client = socket.accept();
        LOG << "Admin connection from " << client->getHost() << ':' << client->getPort();

        bool loggedIn = false;
        unsigned i = 3;
        while(i-- > 0) {
            const LoginPacket p = proto.readSpecificPacket<LoginPacket>(client);
            if(p.getUsername() == "admin" && p.getPassword() == "admin") {
                proto.write(client, LoginResponsePacket(""));
                loggedIn = true;
                break;
            } else {
                proto.write(client, LoginResponsePacket("Wrong login or password: " + std::to_string(i) + " attempts remaining"));
            }
        }

        if(!loggedIn) {
            LOG << Logger::Warning << "Connection failed closing link";
            client->close();
            continue;
        }

        try {
            while(true) {
                proto.read(client);
            }
        } catch(IOError e) {
            LOG << Logger::Warning << "IOError from " << client->getHost() << ':' << client->getPort() << ": " << e.what();
        }
    }
}

void Admin::close() {
    if(closed.exchange(true)) {
        return;
    }

    if(currentAdmin) {
        currentAdmin->close();
    }
    socket.close();
    thread.join();
}
