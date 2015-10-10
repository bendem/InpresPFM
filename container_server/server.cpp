#include <fstream>

#include "server/ContainerServer.hpp"

int main(int argc, char** argv) {
    // Logging setup
    std::cout << std::showbase;
    Logger::instance.addHandler(Logger::consoleHandler);

    // TODO Setup a file logger?
    // TODO More args (and an argument parser?)

    unsigned short port = 31060; // 31060 -> 31069
    if(argc >= 2) {
        port = atoi(argv[1]);
    }

    LOG << "Creating thread pool";
    ThreadPool pool(5, [] {
        LOG << Logger::Debug << "Starting thread from pool";
    });

    LOG << "Creating server";
    ContainerServer server(port, "data/users.csv", pool);

    LOG << "Starting up";
    server.init().listen();

    LOG << "Application end";

    return 0;
}
