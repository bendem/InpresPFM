#include "client/ContainerClient.hpp"
#include "utils/ProgramProperties.hpp"

int main(int argc, char** argv) {
    Logger::instance
        .clearHandlers()
        .addHandler(Logger::consoleHandler(Logger::Warning | Logger::Error))
        .addHandler(Logger::fileHandler("client-debug.log"))
        .addHandler(Logger::fileHandler("client.log", Logger::Warning | Logger::Error));

    ProgramProperties props(argc, argv);

    if(props.has("h") || props.has("help")) {
        std::cout << "Usage: " << argv[0]
            << " --config=<config-file> --port=<port> --host=<host>" << std::endl;
        return 0;
    }


    unsigned short urgency_port = props.getAsUnsignedShort("containerserver.urgency.port", 31070);
    unsigned short port = props.getAsUnsignedShort("containerserver.port", 31060);
    std::string host = props.get("containerserver.host", "localhost");

    LOG << "Urgency connecting";
    std::shared_ptr<Socket> urgency_socket(new Socket);
    urgency_socket->connect(urgency_port, host);
    std::thread urgency_thread([&urgency_socket] {
        while(!urgency_socket->isClosed()) {
            std::stringstream ios;
            urgency_socket->read(sizeof(uint16_t), ios);
            uint16_t len = StreamUtils::read<uint16_t>(ios);

            urgency_socket->accumulate(len, ios);
            std::string message(len, '\0');
            for(uint16_t i = 0; i < len; ++i) {
                message[i] = ios.get();
            }
            std::cout << "\033[s\033[1;1H"
                << std::string(100, ' ')
                << "\033[1;1H"
                << " === SERVER MESSAGE === " << message
                << "\033[u";
            std::cout.flush();
        }
    });
    urgency_thread.detach();

    std::shared_ptr<Socket> s(new Socket);
    s->connect(port, host);

    ProtocolHandler<Translator, PacketId> proto;

    ContainerClient client(s, proto);

    client.init().mainLoop();

    LOG << "Closing urgency socket";
    urgency_socket->close();

    LOG << "done";

    return 0;
}
