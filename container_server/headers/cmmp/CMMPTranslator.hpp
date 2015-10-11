#ifndef CONTAINER_SERVER_CMMPTRANSLATOR_HPP
#define CONTAINER_SERVER_CMMPTRANSLATOR_HPP

#include "cmmp/InputDonePacket.hpp"
#include "cmmp/InputDoneResponsePacket.hpp"
#include "cmmp/InputTruckPacket.hpp"
#include "cmmp/InputTruckResponsePacket.hpp"
#include "cmmp/LoginPacket.hpp"
#include "cmmp/LoginResponsePacket.hpp"
#include "cmmp/LogoutPacket.hpp"
#include "cmmp/LogoutResponsePacket.hpp"
#include "cmmp/OutputDonePacket.hpp"
#include "cmmp/OutputDoneResponsePacket.hpp"
#include "cmmp/OutputOnePacket.hpp"
#include "cmmp/OutputOneResponsePacket.hpp"
#include "cmmp/OutputReadyPacket.hpp"
#include "cmmp/OutputReadyResponsePacket.hpp"
#include "cmmp/PacketId.hpp"
#include "net/Socket.hpp"
#include "protocol/ProtocolError.hpp"

class CMMPTranslator {

public:
    void decode(PacketId id, std::istream&, std::shared_ptr<Socket>);

    template<class T>
    void encode(const T& item, std::ostream&);

};

template<class T>
void CMMPTranslator::encode(const T& item, std::ostream& v) {
    item.encode(v);
}

#endif
