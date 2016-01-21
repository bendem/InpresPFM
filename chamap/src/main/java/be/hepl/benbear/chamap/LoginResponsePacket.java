package be.hepl.benbear.chamap;

import be.hepl.benbear.commons.protocol.Packet;

public class LoginResponsePacket implements Packet {

    private final boolean success;

    public LoginResponsePacket(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    @Override
    public byte getId() {
        return 2;
    }
}
