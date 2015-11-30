#include <csignal>

#include "admin/Admin.hpp"
#include "admin/UrgencyServer.hpp"
#include "utils/ProgramProperties.hpp"

void reset(int sig) {
    std::signal(sig, SIG_DFL);
}

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

    // I have no idea what this does
    struct sigaction sig_action;
    sig_action.sa_handler = reset;
    sigemptyset(&sig_action.sa_mask);
    //sig_action.sa_flags = 0;
    sig_action.sa_restorer = NULL;
    if(sigaction(SIGINT, &sig_action, NULL) == -1) {
        LOG << Logger::Error << "Failed to sigaction";
        return 1;
    }

    unsigned short port = props.getAsUnsignedShort("containerserver.port", 31060); // 31060 -> 31069
    std::string parc_file = props.get("containerserver.parc_file", "data/parc.dat");
    std::string user_file = props.get("containerserver.user_file", "data/users.csv");

    LOG << "Creating thread pool";
    ThreadPool pool(4, [] {
        LOG << Logger::Debug << "Starting thread from pool";
    });

    unsigned short urgency_port = props.getAsUnsignedShort("containerserver.urgency.port", 31070);
    UrgencyServer urgency_server(urgency_port);

    LOG << "Creating server";
    ContainerServer server(port, parc_file, user_file, pool, urgency_server);

    unsigned short admin_port = props.getAsUnsignedShort("containerserver.admin.port", 31069);
    Admin admin(server, admin_port);

    LOG << "Starting up";
    server.init().listen();

    LOG << "Server stopped listening";

    admin.close();

    LOG << "Application end";

    return 0;
}
