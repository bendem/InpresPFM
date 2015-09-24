#include "cmmp/OutputReadyResponsePacket.hpp"

OutputReadyResponsePacket OutputReadyResponsePacket::decode(const std::vector<char>& v) {
    std::vector<char>::const_iterator it = v.begin();
    bool ok = readPrimitive<bool>(it);
    std::string reason = ok ? "" : readString(it);
    uint32_t size = ok ? readPrimitive<uint32_t>(it) : 0;
    std::vector<uint32_t> containerIds;
    if (size) {
        for(uint32_t i = 0; i < size; i++)
            containerIds.push_back(readPrimitive<uint32_t>(it));
    }

    return OutputReadyResponsePacket(ok, size, containerIds, reason);
}

void OutputReadyResponsePacket::encode(std::vector<char>& v) const {
    writePrimitive(v, ok);
    if(!ok) {
        writeString(v, reason);
    } else {
        writePrimitive(v, size);
        for(auto value : containerIds) writePrimitive(v, value);
    }
}
