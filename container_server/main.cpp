#include <iostream>

#include <threading/ThreadPool.hpp>

#include "cmmp/CMMPTranslator.hpp"
#include "protocol/ProtocolHandler.hpp"

int main(int argc, char** argv) {
    /*
    ThreadPool threadPool(2);

    threadPool.submit([] {
        for(unsigned i = 0; i < 10; ++i) {
            std::cout << "hey" << std::endl;
        }
    });

    threadPool.submit([] {
        for(unsigned i = 0; i < 10; ++i) {
            std::cout << "oh" << std::endl;
        }
    });
    */

    unsigned short port = 3069;
    if(argc >= 2) {
        port = atoi(argv[1]);
    }

    Socket s;
    std::cout << "socket" << std::endl;
    s.bind(port);
    std::cout << "bound: " << s.getHandle() << std::endl;

    CMMPTranslator translator;
    ProtocolHandler<CMMPTranslator, PacketId> proto(translator);

    Socket socket = s.accept();
    std::cout << "accepted: " << socket.getHandle() << std::endl;

    LoginPacket::registerHandler([&socket, &proto](LoginPacket packet) {
        std::cout << "Received packet: " << (int) packet.getId() << std::endl;
        proto.write(socket, LoginResponsePacket(false, "bleh"));
    });
    InputTruckPacket::registerHandler([&socket, &proto](InputTruckPacket packet) {
        std::cout << "Received packet: " << (int) packet.getId() << std::endl;
        proto.write(socket, InputTruckResponsePacket(false, std::vector<Container>(), "heh")); // I touched this it probably doesn't work anymore
    });
    InputDonePacket::registerHandler([&socket, &proto](InputDonePacket packet) {
        std::cout << "Received packet: " << (int) packet.getId() << std::endl;
        proto.write(socket, InputDoneResponsePacket(true, ""));
    });
    OutputReadyPacket::registerHandler([&socket, &proto](OutputReadyPacket packet) {
        std::cout << "Received packet: " << (int) packet.getId() << std::endl;
        proto.write(socket, OutputReadyResponsePacket(true, {"conainer-id", "another-id"}, ""));
    });
    OutputOnePacket::registerHandler([&socket, &proto](OutputOnePacket packet) {
        std::cout << "Received packet: " << (int) packet.getId() << std::endl;
        proto.write(socket, OutputOneResponsePacket(true, ""));
    });
    OutputDonePacket::registerHandler([&socket, &proto](OutputDonePacket packet) {
        std::cout << "Received packet: " << (int) packet.getId() << std::endl;
        proto.write(socket, OutputDoneResponsePacket(false, "WRONG!"));
    });
    LogoutPacket::registerHandler([&socket, &proto](LogoutPacket packet) {
        std::cout << "Received packet: " << (int) packet.getId() << std::endl;
        proto.write(socket, LogoutResponsePacket(true, ""));
    });

    for(int i = 0; i < 7; ++i) {
        proto.read(socket);
    }
    //proto.read(socket);
    std::cout << "read" << std::endl;

    /*
    std::vector<char> v;
    char i = 42;
    Translator::writePrimitive(v, i);
    std::cout << v.size() << std::endl;
    for(char c : v) {
        std::cout << (int) c << " ";
    }
    std::cout << std::endl;

    std::vector<char>::const_iterator it = v.begin();
    std::cout << Translator::readPrimitive<char>(it) << std::endl;
    */

    /*
    std::cerr << "creating socket" << std::endl;
    Socket s(8080);

    std::cerr << "listen + accept" << std::endl;
    Socket connection = s.listen(3).accept();
    std::vector<char> arr = {'h', 'e', 'y'};
    connection.write(arr);

    arr = std::move(connection.read(50));
    arr.push_back(0);
    std::cout << arr.data() << std::endl;

    */

    return 0;
}
