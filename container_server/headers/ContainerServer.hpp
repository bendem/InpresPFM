#ifndef CONTAINER_SERVER_SERVER_HPP
#define CONTAINER_SERVER_SERVER_HPP

#include <unordered_set>

#include "SelectorThread.hpp"
#include "cmmp/CMMPTranslator.hpp"
#include "net/Selector.hpp"
#include "net/Socket.hpp"
#include "protocol/ProtocolHandler.hpp"
#include "threading/ThreadPool.hpp"
#include "utils/Logger.hpp"

class ContainerServer {

public:
    ContainerServer(unsigned short, ThreadPool&);
    ~ContainerServer();

    /**
     * Registers all the packet handlers and sets the modules up
     */
    ContainerServer& init();

    /**
     * Starts listening for connections (very blocking)
     */
    ContainerServer& listen();

    /**
     * Stops accepting new connections and unblocks listen.
     */
    void close();

private:
    ThreadPool& pool;
    ProtocolHandler<CMMPTranslator, PacketId> proto;
    Socket socket;
    std::mutex connectionsMutex;
    Selector selector;
    SelectorThread<CMMPTranslator, PacketId> selectorThread;
    std::atomic_bool closed;

};

#endif
