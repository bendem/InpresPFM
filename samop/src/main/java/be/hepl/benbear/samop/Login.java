package be.hepl.benbear.samop;

import be.hepl.benbear.commons.protocol.Packet;

public class Login implements Packet {
    public static final byte ID = 5;

    private final String username;
    private final String password;

    public Login(String username, String password) {
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
