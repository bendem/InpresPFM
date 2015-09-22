#ifndef CPP_COMMONS_PROTOCOLHANDLER_HPP
#define CPP_COMMONS_PROTOCOLHANDLER_HPP

#include <functional>
#include <map>
#include <queue>
#include <vector>

#include "net/Socket.hpp"

template<class Translator>
class ProtocolHandler {

public:
    ProtocolHandler(const Translator& translator) : translator(translator) {}

    template<class T>
    T read(Socket&);

    template<class T>
    ProtocolHandler<Translator>& write(Socket&, T&);

private:
    Translator translator;

    static const char FRAME_END;

    static uint32_t parseLength(std::vector<char>::const_iterator);

};

template<class Translator>
const char ProtocolHandler<Translator>::FRAME_END = 0x42;

template<class Translator>
template<class T>
T ProtocolHandler<Translator>::read(Socket& socket) {
    char id;
    uint32_t len;
    std::vector<char> v(4);

    socket.accumulate(4, v);
    id = v[0];
    len = this->parseLength(++v.begin()) + 1; // + 1 => end frame marquer

    v.clear();
    v.reserve(len);
    socket.accumulate(len, v);

    if(v.back() != FRAME_END) {
        throw std::runtime_error("Invalid frame"); // TODO Custom exception
    }
    v.pop_back();

    return this->translator.decode(id, v);
}

template<class Translator>
template<class T>
ProtocolHandler<Translator>& ProtocolHandler<Translator>::write(Socket& socket, T& item) {
    std::vector<char> v;

    v.push_back(item.getId());
    // TODO Write len as uint32_t

    const std::vector<char>& x = this->translator.encode(item);
    v.insert(v.end(), x.begin(), x.end());
    v.push_back(FRAME_END);
    socket.write(v);

    return *this;
}

template<class Translator>
uint32_t ProtocolHandler<Translator>::parseLength(std::vector<char>::const_iterator iterator) {
    return *reinterpret_cast<uint32_t*>(&iterator);
}

#endif //CPP_COMMONS_PROTOCOLHANDLER_HPP
