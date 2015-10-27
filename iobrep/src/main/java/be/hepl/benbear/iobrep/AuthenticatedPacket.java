package be.hepl.benbear.iobrep;

import java.util.UUID;

public abstract class AuthenticatedPacket extends Packet {

    protected final UUID session;

    protected AuthenticatedPacket(PacketId id, UUID session) {
        super(id);
        this.session = session;
    }

    public UUID getSession() {
        return session;
    }
}
