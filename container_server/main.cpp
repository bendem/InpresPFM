#include "ContainerServer.hpp"

int main(int argc, char** argv) {
    std::cout << std::showbase;
    Logger::instance.addHandler(Logger::consoleHandler);

    unsigned short port = 31060; // 31060 -> 31069
    if(argc >= 2) {
        port = atoi(argv[1]);
    }

    LOG << "Creating thread pool";
    ThreadPool pool(5, [] {
        LOG << "starting thread from pool";
    });

    LOG << "Creating server";
    ContainerServer server(port, pool);

    LOG << "Starting up";
    server.init().listen();

    LOG << "Application end";

    return 0;
}
