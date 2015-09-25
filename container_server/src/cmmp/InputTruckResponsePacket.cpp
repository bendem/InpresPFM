#include "cmmp/InputTruckResponsePacket.hpp"

InputTruckResponsePacket InputTruckResponsePacket::decode(std::vector<char>::const_iterator& it) {
    bool ok = readPrimitive<bool>(it);
    uint32_t size;
    std::string reason;
    std::vector<Container> containers;

    if (ok) {
        size = readPrimitive<uint32_t>(it);
        if (size) {
            for(uint32_t i = 0; i < size; i++)
                containers.push_back(Container(readString(it),
                                               readString(it),
                                               std::make_pair(readPrimitive<uint32_t>(it),
                                                              readPrimitive<uint32_t>(it)))); // No way this works right ?
        }
        reason = "";
    } else {
        reason = readString(it);
        containers.clear();
    }

    return InputTruckResponsePacket(ok, containers, reason);
}

void InputTruckResponsePacket::encode(std::vector<char>& v) const {
    writePrimitive(v, ok);
    if(!ok) {
        writeString(v, reason);
    } else {
        writePrimitive<uint32_t>(v, containers.size());
        for(auto cont : containers) {
            writeString(v, cont.getId());
            writeString(v, cont.getDestination());
            writePrimitive(v, cont.getX());
            writePrimitive(v, cont.getY());
        }
    }
}
