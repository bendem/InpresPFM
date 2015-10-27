package be.hepl.benbear.iobrep;

import java.util.UUID;

public class BoatArrivedPacket extends AuthenticatedPacket {

    private final String boatId;
    private final String destination;

    public BoatArrivedPacket(UUID session, String boatId, String destination) {
        super(PacketId.BOAT_ARRIVED, session);
        this.boatId = boatId;
        this.destination = destination;
    }

    public String getDestination() {
        return destination;
    }

    public String getBoatId() {
        return boatId;
    }

}
