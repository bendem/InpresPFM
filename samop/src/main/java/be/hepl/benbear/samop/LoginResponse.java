package be.hepl.benbear.samop;

import be.hepl.benbear.commons.protocol.Packet;

public class LoginResponse implements Packet {
    public static final byte ID = 6;

    private final boolean ok;
    private final String reason;

    public LoginResponse(boolean ok, String reason) {
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
