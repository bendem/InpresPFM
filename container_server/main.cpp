#include <iostream>
#include <threading/ThreadPool.hpp>

#include "cmmp/CMMPTranslator.hpp"
#include "protocol/ProtocolHandler.hpp"

int main(int argc, char** argv) {
    ThreadPool threadPool(2);
    std::cout << "hi" << std::endl;

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

    /*
    unsigned short port = 3069;
    if(argc >= 2) {
        port = atoi(argv[1]);
    }

    CMMPTranslator translator;
    ProtocolHandler<CMMPTranslator, PacketId> proto(translator);

    LoginPacket::registerHandler([](LoginPacket p) {
        std::cout << p.getUsername() << " logged in with " << p.getPassword() << std::endl;
    });

    LogoutPacket::registerHandler([&proto](LogoutPacket p) {
        std::cout << p.getUsername() << " logged out with " << p.getPassword() << std::endl;
        proto.close();
    });

    Socket s;
    std::cout << "socket" << std::endl;
    s.bind(port);
    std::cout << "bound: " << s.getHandle() << std::endl;
    Socket socket = s.accept();
    std::cout << "accepted: " << socket.getHandle() << std::endl;

    proto.read(socket);
    std::cout << "read" << std::endl;

    /*
    ProtocolHandler<CMMPTranslator> proto(CMMPTranslator());

    proto.read()

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
