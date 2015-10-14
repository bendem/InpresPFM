#ifndef CONTAINER_SERVER_PACKETID_HPP
#define CONTAINER_SERVER_PACKETID_HPP

#include <cstdint>

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

#endif
