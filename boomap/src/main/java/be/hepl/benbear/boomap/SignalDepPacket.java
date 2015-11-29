package be.hepl.benbear.boomap;

import be.hepl.benbear.commons.protocol.Packet;

public class SignalDepPacket implements Packet{
    public static final byte ID = 9;

    private final String transporterId;
    private final String[] containerIds;

    public SignalDepPacket(String transporterId, String[] containerIds) {
        this.transporterId = transporterId;
        this.containerIds = containerIds;
    }

    public String getTransporterId() {
        return transporterId;
    }

    public String[] getContainerIds() {
        return containerIds;
    }

    @Override
    public byte getId() {
        return ID;
    }
}
