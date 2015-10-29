package be.hepl.benbear.iobrep;

public class InvalidSessionResponsePacket extends ResponsePacket {

    private final AuthenticatedPacket packet;

    public InvalidSessionResponsePacket(AuthenticatedPacket packet) {
        super(PacketId.INVALID_SESSION_RESPONSE, "The session expired");
        this.packet = packet;
    }

    public AuthenticatedPacket getPacket() {
        return packet;
    }

}
