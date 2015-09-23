#ifndef CPP_COMMONS_PACKET_HPP
#define CPP_COMMONS_PACKET_HPP

#include <functional>
#include <vector>

template<class P>
using PacketHandler = std::function<void(P)>;

template<class P>
class Packet {

// Actual packet stuff
public:
    Packet(char id) : id(id) {}

private:
    char id;

// Event handling
public:
    static void registerHandler(PacketHandler<P> handler);
    void handle() const;

private:
    static std::vector<PacketHandler<P>> handlers;

// encode/decode helpers
protected:
    template<class T>
    static T readPrimitive(std::vector<char>::const_iterator&);
    static std::string readString(std::vector<char>::const_iterator&);

    template<class T>
    static void writePrimitive(std::vector<char>&, T);
    static void writeString(std::vector<char>&, const std::string&);

};

template<class P>
std::vector<PacketHandler<P>> Packet<P>::handlers;

template<class P>
void Packet<P>::registerHandler(PacketHandler<P> handler) {
    handlers.push_back(handler);
}

template<class P>
void Packet<P>::handle() const {
    for(auto handler : handlers) {
        handler(static_cast<const P&>(*this));
    }
}

template<class P>
template<class T>
T Packet<P>::readPrimitive(std::vector<char>::const_iterator& it) {
    const T* p = reinterpret_cast<const T*>(&*it);
    it += sizeof(T);
    return *p;
}

template<class P>
std::string Packet<P>::readString(std::vector<char>::const_iterator& it) {
    uint32_t len = readPrimitive<uint32_t>(it);
    std::string res;
    for (uint32_t i = 0; i < len; ++i) {
        res += *it;
        ++it;
    }
    return res;
}

template<class P>
template<class T>
void Packet<P>::writePrimitive(std::vector<char>& v, T p) {
    const char* c = reinterpret_cast<const char*>(&p);

    for (unsigned int i = 0; i < sizeof(T); ++i) {
        v.push_back(c[i]);
    }
}

template<class P>
void Packet<P>::writeString(std::vector<char>& v, const std::string& str) {
    writePrimitive<uint32_t>(v, str.size());
    for(char c : str) {
        v.push_back(c);
    }
}

#endif //CPP_COMMONS_PACKET_HPP
