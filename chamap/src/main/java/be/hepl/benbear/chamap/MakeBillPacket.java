package be.hepl.benbear.chamap;

import be.hepl.benbear.commons.protocol.Packet;

public class MakeBillPacket implements Packet {

    private final String transporterId;
    private final String[] containerIds;

    public MakeBillPacket(String transporterId, String[] containerIds) {
        this.transporterId = transporterId;
        this.containerIds = containerIds;
    }

    @Override
    public byte getId() {
        return 3;
    }
}
