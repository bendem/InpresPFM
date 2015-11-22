package be.hepl.benbear.pfmcop;

import be.hepl.benbear.commons.protocol.Packet;

public class LoginPacket implements Packet {

    public static final byte ID = 1;

    public static byte digest(String password) {
        int sum = 0;
        for(int i = 0; i < password.length(); sum += password.charAt(i), ++i);
        return (byte) (sum % 67);
    }

    private final String username;
    private final byte digest;

    public LoginPacket(String username, String password) {
        this(username, digest(password));
    }

    public LoginPacket(String username, byte digest) {
        this.username = username;
        this.digest = digest;
    }

    @Override
    public byte getId() {
        return ID;
    }

    public String getUsername() {
        return username;
    }

    public byte getDigest() {
        return digest;
    }
}
