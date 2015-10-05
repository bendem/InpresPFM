#include "client/ContainerClient.hpp"

int main(int argc, char** argv) {
    unsigned short port = 3069;
    if(argc >= 2) {
        port = atoi(argv[1]);
    }

    std::shared_ptr<Socket> s(new Socket);
    s->connect(port, "localhost");

    CMMPTranslator translator;
    ProtocolHandler<CMMPTranslator, PacketId> proto(translator);

    ContainerClient client(s, proto);

    client.init().mainLoop();

    LOG << "done";

    return 0;
}
