#include "cmmp/InputTruckResponsePacket.hpp"

InputTruckResponsePacket InputTruckResponsePacket::decode(std::vector<char>::const_iterator& it) {
    bool ok = readPrimitive<bool>(it);
    std::string reason;
    std::vector<Container> containers;

    if (ok) {
        uint32_t size = readPrimitive<uint32_t>(it);
        if (size) {
            std::string container_id;
            std::string destination;
            uint32_t x;
            uint32_t y;

            for(uint32_t i = 0; i < size; i++) {
                container_id = readString(it);
                destination = readString(it);
                x = readPrimitive<uint32_t>(it);
                y = readPrimitive<uint32_t>(it);
                containers.push_back(Container(container_id, destination, std::make_pair(x, y)));
            }
        }
    } else {
        reason = readString(it);
    }

    return InputTruckResponsePacket(ok, containers, reason);
}

void InputTruckResponsePacket::encode(std::vector<char>& v) const {
    writePrimitive(v, ok);
    if(!ok) {
        writeString(v, reason);
    } else {
        writePrimitive<uint32_t>(v, containers.size());
        for(const Container& cont : containers) {
            writeString(v, cont.getId());
            writeString(v, cont.getDestination());
            writePrimitive(v, cont.getX());
            writePrimitive(v, cont.getY());
        }
    }
}
