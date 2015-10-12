#ifndef CPP_COMMONS_SOCKET_HPP
#define CPP_COMMONS_SOCKET_HPP

#include <atomic>
#include <cstring>
#include <string>

#include <netdb.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <unistd.h>

#include "net/IOError.hpp"
#include "utils/Logger.hpp"

/**
 * A socket class wraps C low level socket code into a thread safe class.
 * An instance of this class cannot be copied or moved, accept returns a
 * shared_ptr for that reason.
 */
class Socket {

public:

    enum CloseReason {
        Gone, Error, Closed
    };

    typedef std::function<void(Socket&, CloseReason)> CloseHandler;

    Socket() : server(false), handle(-1) {}
    Socket(const Socket&) = delete;
    Socket(Socket&&) = delete;
    ~Socket();

    /**
     * Connects this socket to a specific hostname and port.
     * @param port
     * @param hostname
     * @return the instance for chaining
     */
    Socket& connect(unsigned short, std::string);

    /**
     * Binds the current socket to a port and all interfaces
     * @param port
     * @return the instance for chaining
     */
    Socket& bind(unsigned short);

    /**
     * Binds the current socket to a port and a hostname.
     * @param port
     * @param hostname
     * @return the instance for chaining
     */
    Socket& bind(unsigned short, std::string);

    /**
     * Accepts a new connection.
     * @return a new socket bound to the new connection
     */
    std::shared_ptr<Socket> accept();

    /**
     * Writes the byte vector onto this socket.
     * @param vector a byte vector
     * @return the number of bytes actually written
     */
    long write(const std::string&);

    /**
     * Reads a certain amount of bytes from this socket.
     * @param count the amount of bytes to try to read
     * @return the bytes actually read
     */
    unsigned read(unsigned int, std::ostream&);

    /**
     * Reads from this socket into a provided vector until its filled
     * with the needed amount of bytes.
     * @param count the amount of bytes wanted
     * @param the vector to fill
     */
    void accumulate(unsigned int, std::ostream&);

    /**
     * Gets the low level handle associated with this instance.
     */
    int getHandle() const { return this->handle; }

    /**
     * Closes the connection handled by this instance.
     */
    void close();

    /**
     * Returns this socket got closed.
     */
    bool isClosed() const { return this->handle < 0; }

    /**
     * Returns whether this socket is a server socket (bind) or a client
     * socket (connect or returned by accept).
     */
    bool isServer() const { return this->server; }

    /**
     * Registers a function to be executed when the socket gets closed.
     * @param the call back to execute
     * @return the instance for chaining
     */
    Socket& registerCloseHandler(CloseHandler);

    std::string getHost() const;

    unsigned short getPort() const;

private:
    bool server;
    int handle;
    std::recursive_mutex handleMutex;
    struct sockaddr addr;
    unsigned int addrLen;
    std::vector<CloseHandler> closeHandlers;

    struct sockaddr_in setupPort(unsigned short);
    struct sockaddr_in setupHostAndPort(unsigned short, std::string);
    Socket& setupSocket(const struct sockaddr_in, bool bind);
    void error(const std::string&, int);
    void checkOpen() const;

    void close(CloseReason);

};

#endif
