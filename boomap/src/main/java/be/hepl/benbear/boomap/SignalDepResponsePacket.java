package be.hepl.benbear.boomap;

import be.hepl.benbear.commons.protocol.Packet;

public class SignalDepResponsePacket implements Packet {
    public static final byte ID = 10;

    private final boolean ok;
    private final String reason;

    public SignalDepResponsePacket(boolean ok, String reason) {
        this.ok = ok;
        this.reason = reason;
    }

    public boolean isOk() {
        return ok;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public byte getId() {
        return ID;
    }
}
