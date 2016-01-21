package be.hepl.benbear.chamap;

import be.hepl.benbear.commons.protocol.Packet;

import java.util.UUID;

public class MakeBillPacket implements Packet {

    private final UUID session;
    private final String transporterId;
    private final String[] containerIds;

    public MakeBillPacket(UUID session, String transporterId, String[] containerIds) {
        this.session = session;
        this.transporterId = transporterId;
        this.containerIds = containerIds;
    }

    public UUID getSession() {
        return session;
    }

    public String getTransporterId() {
        return transporterId;
    }

    public String[] getContainerIds() {
        return containerIds;
    }

    @Override
    public byte getId() {
        return 3;
    }

}
