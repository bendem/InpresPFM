#include <iostream>
#include <chrono>
#include <thread>

#include "cmmp/CMMPTranslator.hpp"
#include "net/Socket.hpp"
#include "protocol/ProtocolHandler.hpp"

int main(int argc, char** argv) {
    unsigned short port = 3069;
    if(argc >= 2) {
        port = atoi(argv[1]);
    }

    Socket s;
    s.connect(port, "localhost");

    CMMPTranslator translator;
    ProtocolHandler<CMMPTranslator, PacketId> proto(translator);

    LogoutPacket p("bendem", "supersecurepassword");
    proto.write(s, p);

    using namespace std::chrono;
    std::this_thread::sleep_for(std::chrono::seconds(2));

    return 0;
}
