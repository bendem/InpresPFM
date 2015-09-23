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

    template<class T>
    T decode(PacketId id, const std::vector<char>&);

    template<class T>
    void encode(const T& item, std::vector<char>&);

};

template<class T>
T CMMPTranslator::decode(PacketId id, const std::vector<char>& v) {
    switch(id) {
        case PacketId::Login:
            return LoginPacket::decode(v);
        case PacketId::InputTruck:
            return InputTruckPacket::decode(v);
        case PacketId::InputDone:
            return InputDonePacket::decode(v);
        case PacketId::OutputReady:
            return OutputReadyPacket::decode(v);
        case PacketId::OutputOne:
            return OutputOnePacket::decode(v);
        case PacketId::OutputDone:
            return OutputDonePacket::decode(v);
        case PacketId::Logout:
            return LogoutPacket::decode(v);
    }

    throw std::runtime_error("Invalid packet id"); // TODO Custom exception
}

template<class T>
void CMMPTranslator::encode(const T& item, std::vector<char>& v) {
    item.encode(v);
}

#endif //CONTAINER_SERVER_CMMPTRANSLATOR_HPP
