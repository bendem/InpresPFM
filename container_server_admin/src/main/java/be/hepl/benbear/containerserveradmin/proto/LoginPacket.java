package be.hepl.benbear.containerserveradmin.proto;

import be.hepl.benbear.commons.protocol.Packet;

public class LoginPacket implements Packet {

    public static final byte ID = 0;

    private final String username;
    private final String password;

    public LoginPacket(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public byte getId() {
        return ID;
    }

}
