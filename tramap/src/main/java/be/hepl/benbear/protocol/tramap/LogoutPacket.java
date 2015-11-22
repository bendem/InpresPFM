package be.hepl.benbear.protocol.tramap;

import be.hepl.benbear.commons.protocol.Packet;

public class LogoutPacket implements Packet {

    public static final byte ID = 9;

    // I'm against this, you should never ask anything on logout
    private final String username;
    private final String password;

    public LogoutPacket(String username, String password) {
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
