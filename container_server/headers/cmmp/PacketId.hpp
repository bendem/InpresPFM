#ifndef CONTAINER_SERVER_PACKETID_HPP
#define CONTAINER_SERVER_PACKETID_HPP

enum PacketId : char {
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
