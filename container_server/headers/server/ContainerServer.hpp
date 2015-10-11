#ifndef CONTAINER_SERVER_SERVER_HPP
#define CONTAINER_SERVER_SERVER_HPP

#include <fstream>
#include <unordered_set>
#include <vector>

#include "ParcLocation.hpp"
#include "cmmp/CMMPTranslator.hpp"
#include "io/BinaryFile.hpp"
#include "io/CSVFile.hpp"
#include "net/Selector.hpp"
#include "net/SelectorThread.hpp"
#include "net/Socket.hpp"
#include "protocol/ProtocolHandler.hpp"
#include "threading/ThreadPool.hpp"
#include "utils/Logger.hpp"

using namespace std::placeholders;

class ContainerServer {

    using Mutex = std::recursive_mutex;
    using Lock = std::lock_guard<Mutex>;
    using string = std::string;

public:
    ContainerServer(unsigned short, const string&, const string&, ThreadPool&);
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

    // Handlers
    void loginHandler(const LoginPacket&, std::shared_ptr<Socket>);
    void inputTruckHandler(const InputTruckPacket&, std::shared_ptr<Socket>);
    void inputDoneHandler(const InputDonePacket&, std::shared_ptr<Socket>);
    void outputReadyHandler(const OutputReadyPacket&, std::shared_ptr<Socket>);
    void outputOneHandler(const OutputOnePacket&, std::shared_ptr<Socket>);
    void outputDoneHandler(const OutputDonePacket&, std::shared_ptr<Socket>);
    void logoutHandler(const LogoutPacket&, std::shared_ptr<Socket>);

    /**
     * Checks whether a socket has already authenticated.
     */
    bool isLoggedIn(std::shared_ptr<Socket>);

private:
    BinaryFile<ParcLocation> containerFile;
    std::vector<ParcLocation> parcLocations;
    Mutex parcLocationsMutex;
    std::unordered_map<Socket*, std::vector<Container>> containersBeingStored;
    Mutex containersBeingStoredMutex;

    CSVFile users;
    Mutex usersMutex;

    ThreadPool& pool;

    ProtocolHandler<CMMPTranslator, PacketId> proto;
    Socket socket;
    Selector selector;
    SelectorThread<CMMPTranslator, PacketId> selectorThread;
    std::atomic_bool closed;

    std::unordered_map<Socket*, string> loggedInUsers;
    Mutex loggedInUsersMutex;

    bool findFreePlace(Container&);

    void cleanupContainersBeingStored(Socket&, Socket::CloseReason);

};

#endif
