#ifndef CONTAINER_SERVER_CSA_LOGINRESPONSEPACKET_HPP
#define CONTAINER_SERVER_CSA_LOGINRESPONSEPACKET_HPP

#include "csa/PacketId.hpp"
#include "protocol/Packet.hpp"

namespace csa {

class LoginResponsePacket : public Packet<LoginResponsePacket> {

public:
    static const PacketId id = PacketId::LoginResponse;

    LoginResponsePacket(const std::string& error) : error(error) {}

    const std::string& getError() const { return error; }

    static LoginResponsePacket decode(std::istream& is) {
        return LoginResponsePacket(StreamUtils::read<std::string>(is));
    }
    void encode(std::ostream& os) const {
        StreamUtils::write(os, error);
    }

private:
    std::string error;

};

}

#endif
