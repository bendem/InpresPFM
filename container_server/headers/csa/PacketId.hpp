#ifndef CONTAINER_SERVER_CSA_PACKETID_HPP
#define CONTAINER_SERVER_CSA_PACKETID_HPP

#include <cstdint>

namespace csa {

enum PacketId : uint8_t {
    Login,
    LoginResponse,
    List,
    ListResponse,
    Pause,
    PauseResponse,
    Stop,
    StopResponse,
};

}

#endif
