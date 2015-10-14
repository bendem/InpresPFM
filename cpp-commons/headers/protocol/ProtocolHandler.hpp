#ifndef CPP_COMMONS_PROTOCOLHANDLER_HPP
#define CPP_COMMONS_PROTOCOLHANDLER_HPP

#include <atomic>

#include "io/StreamUtils.hpp"
#include "net/Socket.hpp"
#include "protocol/ProtocolError.hpp"
#include "utils/Logger.hpp"

/**
 * Class abstracting the network implementation of a variable length protocol using limited framing.
 * <Translator> Class handling the transformation of a packet id and a vector of chars to useable packets
 * <Id>         Type of the packet ids (allows enum usage). The size of the id must be exactly 1 byte.
 */
template<class Translator, class Id>
class ProtocolHandler {

    using len_t = uint16_t;
    static const len_t MAX_LEN = UINT16_MAX;

public:
    ProtocolHandler(const Translator& translator) : translator(translator), closed(false) {
        static_assert(sizeof(Id) == 1, "Can only use ProtocolHandler with 1 byte ids");
    }

    void read(std::shared_ptr<Socket>);

    template<class P>
    P readSpecificPacket(std::shared_ptr<Socket>);

    template<class T>
    ProtocolHandler<Translator, Id>& write(std::shared_ptr<Socket>, const T&);

    void close() { closed = true; }

private:
    Translator translator;
    std::atomic_bool closed;

    std::pair<Id, std::string> readPacket(std::shared_ptr<Socket>);

    static const uint8_t FRAME_END;

};

template<class Translator, class Id>
const uint8_t ProtocolHandler<Translator, Id>::FRAME_END = 0x42;

template<class Translator, class Id>
std::pair<Id, std::string> ProtocolHandler<Translator, Id>::readPacket(std::shared_ptr<Socket> socket) {
    Id id;
    len_t len;

    std::stringstream ios;
    socket->accumulate(sizeof(len_t) + 1, ios);

    id = static_cast<Id>(StreamUtils::read<uint8_t>(ios));
    len = StreamUtils::read<len_t>(ios) + 1; // + 1 => end frame marquer

    LOG << Logger::Debug << "Packet received: id:" << id << ":len:" << len;

    std::stringstream os;
    socket->accumulate(len, os);
    std::string packet = os.str();

    if (packet.back() != FRAME_END) {
        throw ProtocolError("Invalid frame end");
    }
    packet.pop_back();
    return { id, packet };
}

template<class Translator, class Id>
void ProtocolHandler<Translator, Id>::read(std::shared_ptr<Socket> socket) {
    Id id;
    std::string chars;

    std::tie(id, chars) = this->readPacket(socket);
    std::istringstream is(chars);
    this->translator.decode(id, is, socket);
}

template<class Translator, class Id>
template<class P>
P ProtocolHandler<Translator, Id>::readSpecificPacket(std::shared_ptr<Socket> socket) {
    while(true) {
        Id id;
        std::string chars;

        std::tie(id, chars) = this->readPacket(socket);

        if(id != P::id) {
            LOG << Logger::Warning << "Ignored invalid packet with id " << id;
            continue;
        }

        std::istringstream is(chars);
        return this->translator.template decodeSpecific<P>(is, socket);
    }
}

template<class Translator, class Id>
template<class T>
ProtocolHandler<Translator, Id>& ProtocolHandler<Translator, Id>::write(std::shared_ptr<Socket> socket, const T& item) {
    std::ostringstream full_packet;
    std::ostringstream packet_only;

    StreamUtils::write(full_packet, static_cast<uint8_t>(T::id));

    this->translator.encode(item, packet_only);
    std::string packet(packet_only.str());

    // Store packet length
    uint64_t long_len = packet.size();
    if(long_len > MAX_LEN) {
        throw std::runtime_error("Packet length too large for the protocol");
    }
    StreamUtils::write(full_packet, static_cast<len_t>(long_len));
    full_packet.write(packet.data(), packet.size());
    StreamUtils::write(full_packet, FRAME_END);

    packet = full_packet.str();

    LOG << Logger::Debug << "id:" << (int) packet[0] << ":len:" << packet.size();

    socket->write(packet);

    return *this;
}

#endif
