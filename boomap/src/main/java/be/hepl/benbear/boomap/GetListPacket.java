package be.hepl.benbear.boomap;

import be.hepl.benbear.commons.protocol.Packet;

public class GetListPacket implements Packet {

    public static final byte ID = 1;

    private final String transporterId;
    private final String destination;
    private final int count;

    public GetListPacket(String transporterId, String destination, int count) {
        this.transporterId = transporterId;
        this.destination = destination;
        this.count = count;
    }


    @Override
    public byte getId() {
        return ID;
    }

    public String getTransporterId() {
        return transporterId;
    }

    public String getDestination() {
        return destination;
    }

    public int getCount() {
        return count;
    }
}
