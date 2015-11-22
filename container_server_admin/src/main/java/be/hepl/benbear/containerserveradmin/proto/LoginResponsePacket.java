package be.hepl.benbear.containerserveradmin.proto;

import be.hepl.benbear.commons.protocol.Packet;

public class LoginResponsePacket implements Packet {

    public static final byte ID = 1;

    private final String error;

    public LoginResponsePacket(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    @Override
    public byte getId() {
        return ID;
    }

}
