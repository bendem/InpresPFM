#ifndef CONTAINER_SERVER_CMMPTRANSLATOR_HPP
#define CONTAINER_SERVER_CMMPTRANSLATOR_HPP

#include "protocol/Translator.hpp"

class CMMPTranslator : public Translator {

public:
    enum class PacketId : char {
        Login,
        InputTruck,
        InputDone,
        OutputReady,
        OutputOne,
        OutputDone,
        Logout,
    };

    CMMPTranslator() {}

    template<class T>
    T decode(PacketId id, std::vector<char>&);

    template<class T>
    std::vector<char> encode(const T& item);

};

template<class T>
T CMMPTranslator::decode(PacketId id, std::vector<char> &vector) {
    switch(id) {
        case PacketId::Login:
            break;
        case PacketId::InputTruck:
            break;
        case PacketId::InputDone:
            break;
        case PacketId::OutputReady:
            break;
        case PacketId::OutputOne:
            break;
        case PacketId::OutputDone:
            break;
        case PacketId::Logout:
            break;
    }

    throw std::runtime_error("Invalid packet id"); // TODO Custom exception
}

template<class T>
std::vector<char> CMMPTranslator::encode(const T& item) {
    return item.encode();
}

#endif //CONTAINER_SERVER_CMMPTRANSLATOR_HPP
