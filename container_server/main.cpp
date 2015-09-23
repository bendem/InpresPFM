#include <iostream>

#include "cmmp/CMMPTranslator.hpp"
#include "protocol/ProtocolHandler.hpp"

int main(int argc, char** argv) {
    unsigned short port = 3069;
    if(argc >= 2) {
        port = atoi(argv[1]);
    }

    CMMPTranslator translator;
    ProtocolHandler<CMMPTranslator, PacketId> proto(translator);
    LoginPacket::registerHandler([](LoginPacket p) {
        std::cout << p.getUsername() << " logged in with " << p.getPassword() << std::endl;
    });
    LogoutPacket::registerHandler([](LogoutPacket p) {
        std::cout << p.getUsername() << " logged in with " << p.getPassword() << std::endl;
    });

    Socket s;
    std::cout << "socket" << std::endl;
    s.bind(port);
    std::cout << "bound" << std::endl;
    Socket socket = s.accept();
    std::cout << "accepted" << std::endl;

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
