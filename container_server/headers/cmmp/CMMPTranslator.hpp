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
            break;
        case PacketId::InputTruck:
            InputTruckPacket::decode(v).handle();
            break;
        case PacketId::InputDone:
            InputDonePacket::decode(v).handle();
            break;
        case PacketId::OutputReady:
            OutputReadyPacket::decode(v).handle();
            break;
        case PacketId::OutputOne:
            OutputOnePacket::decode(v).handle();
            break;
        case PacketId::OutputDone:
            OutputDonePacket::decode(v).handle();
            break;
        case PacketId::Logout:
            LogoutPacket::decode(v).handle();
            break;
    }

    throw std::runtime_error("Invalid packet id"); // TODO Custom exception
}

template<class T>
void CMMPTranslator::encode(const T& item, std::vector<char>& v) {
    item.encode(v);
}

#endif //CONTAINER_SERVER_CMMPTRANSLATOR_HPP
