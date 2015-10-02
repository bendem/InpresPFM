#include "cmmp/OutputReadyResponsePacket.hpp"

OutputReadyResponsePacket OutputReadyResponsePacket::decode(std::vector<char>::const_iterator& it) {
    bool ok = readPrimitive<bool>(it);
    std::string reason = ok ? "" : readString(it);
    uint32_t size = ok ? readPrimitive<uint32_t>(it) : 0;
    std::vector<std::string> containerIds;
    if (size) {
        for(uint32_t i = 0; i < size; i++) {
            containerIds.push_back(readString(it));
        }
    }

    return OutputReadyResponsePacket(ok, containerIds, reason);
}

void OutputReadyResponsePacket::encode(std::vector<char>& v) const {
    writePrimitive(v, ok);
    if(!ok) {
        writeString(v, reason);
    } else {
        writePrimitive<uint32_t>(v, containerIds.size());
        for(auto value : containerIds) {
            writeString(v, value);
        }
    }
}
