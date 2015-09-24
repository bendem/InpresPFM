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

    LoginPacket::registerHandler([&s, &proto](LoginPacket packet) {
        std::cout << "Received packet: " << (int) packet.getId() << std::endl;
        proto.write(s, LoginResponsePacket(false, "bleh"));
    });
    InputTruckPacket::registerHandler([&s, &proto](InputTruckPacket packet) {
        std::cout << "Received packet: " << (int) packet.getId() << std::endl;
        proto.write(s, InputTruckResponsePacket(false, 0, 0, "heh"));
    });
    InputDonePacket::registerHandler([&s, &proto](InputDonePacket packet) {
        std::cout << "Received packet: " << (int) packet.getId() << std::endl;
        proto.write(s, InputDoneResponsePacket(true, ""));
    });
    OutputReadyPacket::registerHandler([&s, &proto](OutputReadyPacket packet) {
        std::cout << "Received packet: " << (int) packet.getId() << std::endl;
        proto.write(s, OutputReadyResponsePacket(true, {"conainer-id", "another-id"}, ""));
    });
    OutputOnePacket::registerHandler([&s, &proto](OutputOnePacket packet) {
        std::cout << "Received packet: " << (int) packet.getId() << std::endl;
        proto.write(s, OutputOneResponsePacket(true, ""));
    });
    OutputDonePacket::registerHandler([&s, &proto](OutputDonePacket packet) {
        std::cout << "Received packet: " << (int) packet.getId() << std::endl;
        proto.write(s, OutputDoneResponsePacket(false, "WRONG!"));
    });
    LogoutPacket::registerHandler([&s, &proto](LogoutPacket packet) {
        std::cout << "Received packet: " << (int) packet.getId() << std::endl;
        proto.write(s, LogoutResponsePacket(true, ""));
    });

    Socket socket = s.accept();
    std::cout << "accepted: " << socket.getHandle() << std::endl;

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
