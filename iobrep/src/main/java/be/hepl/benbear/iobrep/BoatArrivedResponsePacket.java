package be.hepl.benbear.iobrep;

public class BoatArrivedResponsePacket extends ResponsePacket {

    public BoatArrivedResponsePacket(String reason) {
        super(PacketId.BOAT_ARRIVED_RESPONSE, reason);
    }

}
