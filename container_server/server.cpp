#include "admin/Admin.hpp"
#include "server/ContainerServer.hpp"
#include "utils/ProgramProperties.hpp"

int main(int argc, char** argv) {
    ProgramProperties props(argc, argv);

    // Logging setup
    std::cout << std::showbase << std::boolalpha;
    std::cerr << std::showbase << std::boolalpha;
    Logger::instance
        .clearHandlers()
        .addHandler(Logger::consoleHandler(props.has("v") ? -1 : Logger::Warning | Logger::Error))
        .addHandler(Logger::fileHandler("server-debug.log"))
        .addHandler(Logger::fileHandler("server.log", Logger::Warning | Logger::Error));

    if(props.has("h") || props.has("help")) {
        std::cout << "Usage: " << argv[0]
            << " --config=<config-file> --port=<port> --parc-file=<parc_file> --user-file=<user_file>" << std::endl;
        return 0;
    }

    unsigned short port = props.getAsUnsignedShort("containerserver.port", 31060); // 31060 -> 31069
    std::string parc_file = props.get("containerserver.parc_file", "data/parc.dat");
    std::string user_file = props.get("containerserver.user_file", "data/users.csv");

    LOG << "Creating thread pool";
    ThreadPool pool(4, [] {
        LOG << Logger::Debug << "Starting thread from pool";
    });

    LOG << "Creating server";
    ContainerServer server(port, parc_file, user_file, pool);

    unsigned short admin_port = props.getAsUnsignedShort("containerserver.admin.port", 31069);
    Admin admin(server, admin_port);

    LOG << "Starting up";
    server.init().listen();

    admin.close();

    LOG << "Application end";

    return 0;
}
