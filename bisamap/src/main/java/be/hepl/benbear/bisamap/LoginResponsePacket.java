package be.hepl.benbear.bisamap;

import be.hepl.benbear.commons.protocol.Packet;

import java.util.UUID;

public class LoginResponsePacket implements Packet {

    public static final LoginResponsePacket ERROR = new LoginResponsePacket(null);

    private final UUID session;

    public LoginResponsePacket(UUID session) {
        this.session = session;
    }

    public boolean wasOk() {
        return session != null;
    }

    public UUID getSession() {
        return session;
    }

    @Override
    public byte getId() {
        return PacketId.LoginResponsePacket.id;
    }

}
