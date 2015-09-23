#ifndef CPP_COMMONS_PROTOCOLHANDLER_HPP
#define CPP_COMMONS_PROTOCOLHANDLER_HPP

#include <iostream>

#include <functional>
#include <map>
#include <queue>
#include <vector>

#include "net/Socket.hpp"

template<class Translator, class Id>
class ProtocolHandler {

public:
    ProtocolHandler(const Translator& translator) : translator(translator) {
        static_assert(sizeof(Id) == 1, "Can only use ProtocolHandler with 1 byte ids");
    }

    void read(Socket&);

    template<class T>
    ProtocolHandler<Translator, Id>& write(Socket&, T&);

private:
    Translator translator;

    static const char FRAME_END;

    static uint32_t parseLength(std::vector<char>::const_iterator);

};

template<class Translator, class Id>
const char ProtocolHandler<Translator, Id>::FRAME_END = 0x42;

template<class Translator, class Id>
void ProtocolHandler<Translator, Id>::read(Socket& socket) {
    Id id;
    uint32_t len;
    std::vector<char> v(5);

    socket.accumulate(5, v);
    id = (Id) v[0];
    len = this->parseLength(++v.begin()) + 1; // + 1 => end frame marquer

    std::cerr << id << ':' << len << ':' << v.size() << " { ";
    for(char c : v) {
        std::cerr << "0x" << std::hex << (int) c << ' ';
    }
    std::cerr << '}' << std::endl;

    v.clear();
    v.reserve(len);
    socket.accumulate(len, v);

    if(v.back() != FRAME_END) {
        throw std::runtime_error("Invalid frame"); // TODO Custom exception
    }
    v.pop_back();

    this->translator.decode(id, v);
}

template<class Translator, class Id>
template<class T>
ProtocolHandler<Translator, Id>& ProtocolHandler<Translator, Id>::write(Socket& socket, T& item) {
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
    std::cout << "1" << std::endl;

    std::cerr << (int) v[0] << ':' << len << ':' << v.size() << " { ";
    for(char c : v) {
        std::cerr << "0x" << std::hex << (int) c << ' ';
    }
    std::cerr << '}' << std::endl;

    socket.write(v);

    return *this;
}

template<class Translator, class Id>
uint32_t ProtocolHandler<Translator, Id>::parseLength(std::vector<char>::const_iterator iterator) {
    return *reinterpret_cast<const uint32_t*>(&*iterator);
}

#endif //CPP_COMMONS_PROTOCOLHANDLER_HPP
