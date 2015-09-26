#include "cmmp/CMMPTranslator.hpp"

void CMMPTranslator::decode(PacketId id, const std::vector<char>& v) {
    std::vector<char>::const_iterator it = v.begin();
    switch(id) {
        case PacketId::Login:
            LoginPacket::decode(it).handle();
            return;
        case PacketId::InputTruck:
            InputTruckPacket::decode(it).handle();
            return;
        case PacketId::InputDone:
            InputDonePacket::decode(it).handle();
            return;
        case PacketId::OutputReady:
            OutputReadyPacket::decode(it).handle();
            return;
        case PacketId::OutputOne:
            OutputOnePacket::decode(it).handle();
            return;
        case PacketId::OutputDone:
            OutputDonePacket::decode(it).handle();
            return;
        case PacketId::Logout:
            LogoutPacket::decode(it).handle();
            return;
        case PacketId::LoginResponse:
            LoginResponsePacket::decode(it).handle();
            return;
        case PacketId::InputTruckResponse:
            InputTruckResponsePacket::decode(it).handle();
            return;
        case PacketId::InputDoneResponse:
            InputDoneResponsePacket::decode(it).handle();
            return;
        case PacketId::OutputReadyResponse:
            OutputReadyResponsePacket::decode(it).handle();
            return;
        case PacketId::OutputOneResponse:
            OutputOneResponsePacket::decode(it).handle();
            return;
        case PacketId::OutputDoneResponse:
            OutputDoneResponsePacket::decode(it).handle();
            return;
        case PacketId::LogoutResponse:
            LogoutResponsePacket::decode(it).handle();
            return;
    }

    throw ProtocolError("Invalid packet id");
}
