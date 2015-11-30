#ifndef CONTAINER_SERVER_SERVER_HPP
#define CONTAINER_SERVER_SERVER_HPP

#include <fstream>
#include <unordered_set>
#include <vector>

#include "ParcLocation.hpp"
#include "admin/UrgencyServer.hpp"
#include "cmmp/Translator.hpp"
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
    ContainerServer(unsigned short, const string&, const string&, ThreadPool&, UrgencyServer&);
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

    std::vector<string> getConnectedIps();

    ContainerServer& togglePause();

    bool close(unsigned);

    // Handlers
    bool prePacketHandler(cmmp::PacketId, uint16_t);
    void loginHandler(const cmmp::LoginPacket&, std::shared_ptr<Socket>);
    void inputTruckHandler(const cmmp::InputTruckPacket&, std::shared_ptr<Socket>);
    void inputDoneHandler(const cmmp::InputDonePacket&, std::shared_ptr<Socket>);
    void outputReadyHandler(const cmmp::OutputReadyPacket&, std::shared_ptr<Socket>);
    void outputOneHandler(const cmmp::OutputOnePacket&, std::shared_ptr<Socket>);
    void outputDoneHandler(const cmmp::OutputDonePacket&, std::shared_ptr<Socket>);
    void logoutHandler(const cmmp::LogoutPacket&, std::shared_ptr<Socket>);

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
    UrgencyServer& urgencyServer;

    ProtocolHandler<cmmp::Translator, cmmp::PacketId> proto;
    Socket socket;
    Selector selector;
    SelectorThread<cmmp::Translator, cmmp::PacketId> selectorThread;
    std::atomic_bool closed;
    std::atomic_bool closing;
    std::atomic_bool paused;

    std::unordered_map<Socket*, string> loggedInUsers;
    Mutex loggedInUsersMutex;

    bool findFreePlace(Container&);

    void cleanupContainersBeingStored(Socket&, Socket::CloseReason);

};

#endif
