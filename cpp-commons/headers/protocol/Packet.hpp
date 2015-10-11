#ifndef CPP_COMMONS_PACKET_HPP
#define CPP_COMMONS_PACKET_HPP

#include <functional>
#include <vector>

#include "io/StreamUtils.hpp"
#include "net/Socket.hpp"

template<class P>
using PacketHandler = std::function<void(const P&, std::shared_ptr<Socket>)>;

/**
 * Facility class to help with converting a network packet to/from a vector of chars
 * and handle callbacks.
 *
 * A packet should implement
 * + static P decode(vector<char>::const_iterator),
 * + void encode(vector<char>&),
 * + static Id id
 */
template<class P>
class Packet {

// Event handling
public:
    static void registerHandler(PacketHandler<P> handler);
    void handle(std::shared_ptr<Socket>) const;

private:
    static std::vector<PacketHandler<P>> handlers;

};

template<class P>
std::vector<PacketHandler<P>> Packet<P>::handlers;

template<class P>
void Packet<P>::registerHandler(PacketHandler<P> handler) {
    handlers.push_back(handler);
}

template<class P>
void Packet<P>::handle(std::shared_ptr<Socket> socket) const {
    for(PacketHandler<P> handler : handlers) {
        handler(static_cast<const P&>(*this), socket);
    }
}

#endif
