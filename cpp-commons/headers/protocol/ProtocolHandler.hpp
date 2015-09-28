#ifndef CPP_COMMONS_PROTOCOLHANDLER_HPP
#define CPP_COMMONS_PROTOCOLHANDLER_HPP

#include <atomic>
#include <functional>
#include <map>
#include <queue>
#include <vector>

#include "net/Socket.hpp"
#include "utils/Logger.hpp"

#include "protocol/ProtocolError.hpp"

template<class Translator, class Id>
class ProtocolHandler {

public:
    ProtocolHandler(const Translator& translator) : translator(translator), closed(false) {
        static_assert(sizeof(Id) == 1, "Can only use ProtocolHandler with 1 byte ids");
    }

    void read(Socket&);

    template<class T>
    ProtocolHandler<Translator, Id>& write(Socket&, const T&);

    void close() { closed = true; }

private:
    Translator translator;
    std::atomic_bool closed;

    static const char FRAME_END;

    static uint32_t parseLength(char*);

};

template<class Translator, class Id>
const char ProtocolHandler<Translator, Id>::FRAME_END = 0x42;

template<class Translator, class Id>
void ProtocolHandler<Translator, Id>::read(Socket& socket) {
    Id id;
    uint32_t len;
    std::vector<char> v;

    socket.accumulate(5, v);
    id = (Id) v[0];
    len = this->parseLength(&v[1]) + 1; // + 1 => end frame marquer

    LOG << Logger::Debug << "Packet received: id:" << id << ":len:" << len << ":read:" << v.size();

    if(len < 1) {
        throw ProtocolError("Invalid length: " + std::to_string(len));
    }

    v.clear();
    v.reserve(len);
    socket.accumulate(len, v);

    if (v.back() != FRAME_END) {
        throw ProtocolError("Invalid frame end");
    }
    v.pop_back();

    this->translator.decode(id, v, socket);
}

template<class Translator, class Id>
template<class T>
ProtocolHandler<Translator, Id>& ProtocolHandler<Translator, Id>::write(Socket& socket, const T& item) {
    std::vector<char> v(5, 0); // Reserve 5 places for the id and the length

    v[0] = item.getId();
    this->translator.encode(item, v);

    // Store packet length
    uint32_t len = v.size() - 5;
    const char* bytes = reinterpret_cast<const char*>(&len);
    v[1] = bytes[0];
    v[2] = bytes[1];
    v[3] = bytes[2];
    v[4] = bytes[3];

    v.push_back(FRAME_END);

    LOG << Logger::Debug << "id:" << (int) v[0] << ":len:" << len << ":written:" << v.size();

    socket.write(v);

    return *this;
}

template<class Translator, class Id>
uint32_t ProtocolHandler<Translator, Id>::parseLength(char* c) {
    return *reinterpret_cast<const uint32_t*>(c);
}

#endif //CPP_COMMONS_PROTOCOLHANDLER_HPP
