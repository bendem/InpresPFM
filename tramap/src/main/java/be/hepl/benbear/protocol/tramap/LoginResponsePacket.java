package be.hepl.benbear.protocol.tramap;

public class LoginResponsePacket {

    private final boolean ok;
    private final String reason;

    public LoginResponsePacket(boolean ok, String reason) {
        this.ok = ok;
        this.reason = reason;
    }

    public boolean isOk() {
        return ok;
    }

    public String getReason() {
        return reason;
    }

}
