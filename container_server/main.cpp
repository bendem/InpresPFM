#include "ContainerServer.hpp"

int main(int argc, char** argv) {
    unsigned short port = 31060; // 31060 -> 31069
    if(argc >= 2) {
        port = atoi(argv[1]);
    }

    Logger::instance.addHandler(Logger::consoleHandler);

    LOG << "Creating thread pool";
    ThreadPool pool(5);

    LOG << "Creating server";
    ContainerServer server(port, pool);

    LOG << "Starting up";
    server.init().listen();

    LOG << "Application end";

    return 0;
}
