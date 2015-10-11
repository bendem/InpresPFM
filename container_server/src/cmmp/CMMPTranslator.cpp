#include "cmmp/CMMPTranslator.hpp"

void CMMPTranslator::decode(PacketId id, std::istream& is, std::shared_ptr<Socket> socket) {
    switch(id) {
        case PacketId::Login:
            LoginPacket::decode(is).handle(socket);
            return;
        case PacketId::InputTruck:
            InputTruckPacket::decode(is).handle(socket);
            return;
        case PacketId::InputDone:
            InputDonePacket::decode(is).handle(socket);
            return;
        case PacketId::OutputReady:
            OutputReadyPacket::decode(is).handle(socket);
            return;
        case PacketId::OutputOne:
            OutputOnePacket::decode(is).handle(socket);
            return;
        case PacketId::OutputDone:
            OutputDonePacket::decode(is).handle(socket);
            return;
        case PacketId::Logout:
            LogoutPacket::decode(is).handle(socket);
            return;
        case PacketId::LoginResponse:
            LoginResponsePacket::decode(is).handle(socket);
            return;
        case PacketId::InputTruckResponse:
            InputTruckResponsePacket::decode(is).handle(socket);
            return;
        case PacketId::InputDoneResponse:
            InputDoneResponsePacket::decode(is).handle(socket);
            return;
        case PacketId::OutputReadyResponse:
            OutputReadyResponsePacket::decode(is).handle(socket);
            return;
        case PacketId::OutputOneResponse:
            OutputOneResponsePacket::decode(is).handle(socket);
            return;
        case PacketId::OutputDoneResponse:
            OutputDoneResponsePacket::decode(is).handle(socket);
            return;
        case PacketId::LogoutResponse:
            LogoutResponsePacket::decode(is).handle(socket);
            return;
    }

    throw ProtocolError("Invalid packet id");
}
