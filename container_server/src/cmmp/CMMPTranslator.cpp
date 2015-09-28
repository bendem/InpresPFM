#include "cmmp/CMMPTranslator.hpp"

void CMMPTranslator::decode(PacketId id, const std::vector<char>& v, Socket& socket) {
    std::vector<char>::const_iterator it = v.begin();
    switch(id) {
        case PacketId::Login:
            LoginPacket::decode(it).handle(socket);
            return;
        case PacketId::InputTruck:
            InputTruckPacket::decode(it).handle(socket);
            return;
        case PacketId::InputDone:
            InputDonePacket::decode(it).handle(socket);
            return;
        case PacketId::OutputReady:
            OutputReadyPacket::decode(it).handle(socket);
            return;
        case PacketId::OutputOne:
            OutputOnePacket::decode(it).handle(socket);
            return;
        case PacketId::OutputDone:
            OutputDonePacket::decode(it).handle(socket);
            return;
        case PacketId::Logout:
            LogoutPacket::decode(it).handle(socket);
            return;
        case PacketId::LoginResponse:
            LoginResponsePacket::decode(it).handle(socket);
            return;
        case PacketId::InputTruckResponse:
            InputTruckResponsePacket::decode(it).handle(socket);
            return;
        case PacketId::InputDoneResponse:
            InputDoneResponsePacket::decode(it).handle(socket);
            return;
        case PacketId::OutputReadyResponse:
            OutputReadyResponsePacket::decode(it).handle(socket);
            return;
        case PacketId::OutputOneResponse:
            OutputOneResponsePacket::decode(it).handle(socket);
            return;
        case PacketId::OutputDoneResponse:
            OutputDoneResponsePacket::decode(it).handle(socket);
            return;
        case PacketId::LogoutResponse:
            LogoutResponsePacket::decode(it).handle(socket);
            return;
    }

    throw ProtocolError("Invalid packet id");
}
