#include <iostream>
#include <cmmp/LogoutPacket.hpp>
#include <cmmp/InputDonePacket.hpp>

#include "protocol/ProtocolHandler.hpp"
#include "cmmp/CMMPTranslator.hpp"

int main(int argc, char** argv) {
    unsigned short port = 3069;
    std::cout << "hey" << std::endl;
    if(argc >= 2) {
        port = atoi(argv[1]);
    }

    CMMPTranslator translator;
    ProtocolHandler<CMMPTranslator, PacketId> proto(translator);
    LoginPacket::registerHandler([](LoginPacket p) {
        std::cout << p.getUsername() << " logged in" << std::endl;
    });

    Socket s;
    s.bind(port);
    Socket socket = s.accept();

    proto.read(socket);

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
