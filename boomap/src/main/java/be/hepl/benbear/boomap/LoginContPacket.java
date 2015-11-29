package be.hepl.benbear.boomap;

import be.hepl.benbear.commons.protocol.Packet;

public class LoginContPacket implements Packet{
    public static final byte ID = 5;

    private final String username;
    private final String password;

    public LoginContPacket(String username, String password) {
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
