package be.hepl.benbear.pidep;

import java.util.UUID;

public abstract class AuthenticatedPacket extends Packet {

    private final UUID session;

    protected AuthenticatedPacket(Id id, UUID session) {
        super(id);
        this.session = session;
    }

    public UUID getSession() {
        return session;
    }

}
