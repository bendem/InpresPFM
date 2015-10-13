#include "client/ContainerClient.hpp"
#include "utils/ProgramProperties.hpp"

int main(int argc, char** argv) {
    ProgramProperties props(argc, argv);

    if(props.has("h") || props.has("help")) {
        std::cout << "Usage: " << argv[0] << " --port=<port> --host=<host>" << std::endl;
        return 0;
    }

    unsigned short port = props.getAsUnsignedShort("port", 31060);
    std::string host = props.get("host", "localhost");

    std::shared_ptr<Socket> s(new Socket);
    s->connect(port, host);

    CMMPTranslator translator;
    ProtocolHandler<CMMPTranslator, PacketId> proto(translator);

    ContainerClient client(s, proto);

    client.init().mainLoop();

    LOG << "done";

    return 0;
}
