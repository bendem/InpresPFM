#ifndef CONTAINER_SERVER_CMMPTRANSLATOR_HPP
#define CONTAINER_SERVER_CMMPTRANSLATOR_HPP

#include "cmmp/PacketId.hpp"
#include "cmmp/InputDonePacket.hpp"
#include "cmmp/InputTruckPacket.hpp"
#include "cmmp/LoginPacket.hpp"
#include "cmmp/LogoutPacket.hpp"
#include "cmmp/OutputDonePacket.hpp"
#include "cmmp/OutputOnePacket.hpp"
#include "cmmp/OutputReadyPacket.hpp"
#include "cmmp/LogoutResponsePacket.hpp"
#include "cmmp/OutputDoneResponsePacket.hpp"
#include "cmmp/OutputOneResponsePacket.hpp"
#include "cmmp/OutputReadyResponsePacket.hpp"
#include "cmmp/InputDoneResponsePacket.hpp"
#include "cmmp/LoginResponsePacket.hpp"
#include "cmmp/InputTruckResponsePacket.hpp"
#include "net/Socket.hpp"
#include "protocol/ProtocolError.hpp"

class CMMPTranslator {

public:
    void decode(PacketId id, const std::vector<char>&, Socket&);

    template<class T>
    void encode(const T& item, std::vector<char>&);

};

template<class T>
void CMMPTranslator::encode(const T& item, std::vector<char>& v) {
    item.encode(v);
}

#endif
