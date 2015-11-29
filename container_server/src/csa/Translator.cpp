#include "csa/Translator.hpp"

namespace csa {

void Translator::decode(PacketId id, std::istream& is, std::shared_ptr<Socket> socket) {
    switch(id) {
        case Login:
            LoginPacket::decode(is).handle(socket);
            break;
        case LoginResponse:
            LoginResponsePacket::decode(is).handle(socket);
            break;
        case List:
            ListPacket::decode(is).handle(socket);
            break;
        case ListResponse:
            ListResponsePacket::decode(is).handle(socket);
            break;
        case Pause:
            PausePacket::decode(is).handle(socket);
            break;
        case PauseResponse:
            PauseResponsePacket::decode(is).handle(socket);
            break;
        case Stop:
            StopPacket::decode(is).handle(socket);
            break;
        case StopResponse:
            StopResponsePacket::decode(is).handle(socket);
            break;
    }
}

}
