#include "client/ContainerClient.hpp"

int main(int argc, char** argv) {
    if(argc > 1 && argv[1] == std::string("-h")) {
        std::cout << "Usage: " << argv[0] << " <port> <target host>" << std::endl;
        return 0;
    }

    unsigned short port = 31060;
    std::string host("localhost");
    if(argc >= 2) {
        port = atoi(argv[1]);
    }
    if(argc >= 3) {
        host = argv[2];
    }

    std::shared_ptr<Socket> s(new Socket);
    s->connect(port, host);

    CMMPTranslator translator;
    ProtocolHandler<CMMPTranslator, PacketId> proto(translator);

    ContainerClient client(s, proto);

    client.init().mainLoop();

    LOG << "done";

    return 0;
}
