#include <iostream>
#include <thread>

#include "cmmp/CMMPTranslator.hpp"
#include "net/Socket.hpp"
#include "protocol/ProtocolHandler.hpp"

int main(int argc, char** argv) {
    unsigned short port = 3069;
    if(argc >= 2) {
        port = atoi(argv[1]);
    }

    std::shared_ptr<Socket> s(new Socket);
    s->connect(port, "localhost");

    CMMPTranslator translator;
    ProtocolHandler<CMMPTranslator, PacketId> proto(translator);

    LoginResponsePacket::registerHandler([](LoginResponsePacket p, std::shared_ptr<Socket>) {
        std::cout << "received: " << (int) p.getId() << std::endl;
    });
    InputTruckResponsePacket::registerHandler([](InputTruckResponsePacket p, std::shared_ptr<Socket>) {
        std::cout << "received: " << (int) p.getId() << std::endl;
    });
    InputDoneResponsePacket::registerHandler([](InputDoneResponsePacket p, std::shared_ptr<Socket>) {
        std::cout << "received: " << (int) p.getId() << std::endl;
    });
    OutputReadyResponsePacket::registerHandler([](OutputReadyResponsePacket p, std::shared_ptr<Socket>) {
        std::cout << "received: " << (int) p.getId() << std::endl;
    });
    OutputOneResponsePacket::registerHandler([](OutputOneResponsePacket p, std::shared_ptr<Socket>) {
        std::cout << "received: " << (int) p.getId() << std::endl;
    });
    OutputDoneResponsePacket::registerHandler([](OutputDoneResponsePacket p, std::shared_ptr<Socket>) {
        std::cout << "received: " << (int) p.getId() << std::endl;
    });
    LogoutResponsePacket::registerHandler([](LogoutResponsePacket p, std::shared_ptr<Socket>) {
        std::cout << "received: " << (int) p.getId() << std::endl;
    });

    LOG << "Sending all packets";

    proto.write(s, LoginPacket("bendem", "supersecurepassword", true));
    s->close();/*
    proto.write(s, InputTruckPacket("license", "container-id"));
    proto.write(s, InputDonePacket(true, 1.2));
    proto.write(s, OutputReadyPacket("license", "destination", 3));
    proto.write(s, OutputOnePacket("cont-id"));
    proto.write(s, OutputDonePacket("contain-id", 3));
    proto.write(s, LogoutPacket("bendem", "supersecurepassword"));


    LOG << "Reading 7 packets";

    for(int i = 0; i < 7; ++i) {
        proto.read(s);
    }
    */

    LOG << "done";

    return 0;
}
