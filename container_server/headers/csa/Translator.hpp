#ifndef CONTAINER_SERVER_TRANSLATOR_HPP
#define CONTAINER_SERVER_TRANSLATOR_HPP

#include <sstream>

#include "csa/LoginPacket.hpp"
#include "csa/LoginResponsePacket.hpp"
#include "csa/ListPacket.hpp"
#include "csa/ListResponsePacket.hpp"
#include "csa/PausePacket.hpp"
#include "csa/PauseResponsePacket.hpp"
#include "csa/StopPacket.hpp"
#include "csa/StopResponsePacket.hpp"
#include "csa/PacketId.hpp"
#include "net/Socket.hpp"

namespace csa {

class Translator {

public:
    void decode(PacketId id, std::istream&, std::shared_ptr<Socket>);

    template<class P>
    P decodeSpecific(std::istream&, std::shared_ptr<Socket>);

    template<class T>
    void encode(const T& item, std::ostream&);

};

template<class P>
P Translator::decodeSpecific(std::istream& is, std::shared_ptr<Socket> s) {
    P decoded = P::decode(is);
    decoded.handle(s);
    return decoded;
}

template<class T>
void Translator::encode(const T& item, std::ostream& v) {
    item.encode(v);
}

}

#endif
