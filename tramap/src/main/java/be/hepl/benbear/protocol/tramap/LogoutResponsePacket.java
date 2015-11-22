package be.hepl.benbear.protocol.tramap;

import be.hepl.benbear.commons.protocol.Packet;

public class LogoutResponsePacket implements Packet{

    public static final byte ID = 10;

    private final boolean ok;
    private final String reason;

    public LogoutResponsePacket(boolean ok, String reason) {
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
