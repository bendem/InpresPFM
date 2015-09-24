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

class CMMPTranslator {

public:
    CMMPTranslator() {}

    void decode(PacketId id, const std::vector<char>&);

    template<class T>
    void encode(const T& item, std::vector<char>&);

};

void CMMPTranslator::decode(PacketId id, const std::vector<char>& v) {
    switch(id) {
        case PacketId::Login:
            LoginPacket::decode(v).handle();
            return;
        case PacketId::InputTruck:
            InputTruckPacket::decode(v).handle();
            return;
        case PacketId::InputDone:
            InputDonePacket::decode(v).handle();
            return;
        case PacketId::OutputReady:
            OutputReadyPacket::decode(v).handle();
            return;
        case PacketId::OutputOne:
            OutputOnePacket::decode(v).handle();
            return;
        case PacketId::OutputDone:
            OutputDonePacket::decode(v).handle();
            return;
        case PacketId::Logout:
            LogoutPacket::decode(v).handle();
            return;
        case PacketId::LoginResponse:
            LoginResponsePacket::decode(v).handle();
            return;
        case PacketId::InputTruckResponse:
            InputTruckResponsePacket::decode(v).handle();
            return;
        case PacketId::InputDoneResponse:
            InputDoneResponsePacket::decode(v).handle();
            return;
        case PacketId::OutputReadyResponse:
            OutputReadyResponsePacket::decode(v).handle();
            return;
        case PacketId::OutputOneResponse:
            OutputOneResponsePacket::decode(v).handle();
            return;
        case PacketId::OutputDoneResponse:
            OutputDoneResponsePacket::decode(v).handle();
            return;
        case PacketId::LogoutResponse:
            LogoutResponsePacket::decode(v).handle();
            return;
    }

    throw std::runtime_error("Invalid packet id"); // TODO Custom exception
}

template<class T>
void CMMPTranslator::encode(const T& item, std::vector<char>& v) {
    item.encode(v);
}

#endif //CONTAINER_SERVER_CMMPTRANSLATOR_HPP
