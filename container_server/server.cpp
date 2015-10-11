#include "server/ContainerServer.hpp"

int main(int argc, char** argv) {
    // Logging setup
    std::cout << std::showbase;
    std::cerr << std::showbase;

    // TODO Setup a file logger?
    // TODO an argument parser?

    unsigned short port = 31060; // 31060 -> 31069
    std::string parc_file = "data/parc.dat";
    std::string user_file = "data/users.csv";
    if(argc >= 2) {
        port = atoi(argv[1]);
    }
    if(argc >= 3) {
        parc_file = argv[2];
    }
    if(argc >= 4) {
        user_file = argv[3];
    }

    LOG << "Creating thread pool";
    ThreadPool pool(5, [] {
        LOG << Logger::Debug << "Starting thread from pool";
    });

    LOG << "Creating server";
    ContainerServer server(port, parc_file, user_file, pool);

    LOG << "Starting up";
    server.init().listen();

    LOG << "Application end";

    return 0;
}
