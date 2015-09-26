#include <iostream>

#include "ContainerServer.hpp"
#include "utils/Logger.hpp"

int main(int argc, char** argv) {
    unsigned short port = 3069;
    if(argc >= 2) {
        port = atoi(argv[1]);
    }

    LOG_INSTANCE.addHandler(Logger::consoleHandler);

    LOG << Logger::Warning << "test";

    //ThreadPool pool(2);
    //ContainerServer server(port, pool);
    //server.init().listen();

    return 0;
}
