package be.hepl.benbear.iobrep;

import java.util.UUID;

public class LoginResponsePacket extends ResponsePacket {

    private final UUID session;

    public LoginResponsePacket(UUID session, String reason) {
        super(PacketId.LOGIN_RESPONSE, reason);
        this.session = session;
    }

    public UUID getSession() {
        return session;
    }

}
