package be.hepl.benbear.iobrep;

public class CheckSessionValidResponsePacket extends ResponsePacket {

    public CheckSessionValidResponsePacket(String reason) {
        super(PacketId.CHECK_VALID_SESSION_RESPONSE, reason);
    }
}
