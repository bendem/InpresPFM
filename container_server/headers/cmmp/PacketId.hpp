#ifndef CONTAINER_SERVER_CMMP_PACKETID_HPP
#define CONTAINER_SERVER_CMMP_PACKETID_HPP

#include <cstdint>

namespace cmmp {

enum PacketId : uint8_t {
    Login,
    InputTruck,
    InputDone,
    OutputReady,
    OutputOne,
    OutputDone,
    Logout,
    LoginResponse,
    InputTruckResponse,
    InputDoneResponse,
    OutputReadyResponse,
    OutputOneResponse,
    OutputDoneResponse,
    LogoutResponse,
};

}

#endif
