package be.hepl.benbear.iobrep;

public abstract class ResponsePacket extends Packet {

    protected final String reason;

    protected ResponsePacket(PacketId id, String reason) {
        super(id);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public boolean isOk() {
        return reason == null;
    }

}
