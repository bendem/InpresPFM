package be.hepl.benbear.chamap;

import be.hepl.benbear.commons.protocol.Packet;

public class LoginPacket implements Packet {

    private final String username;
    private final long time;
    private final byte[] salt;
    private final byte[] digest;

    public LoginPacket(String username, long time, byte[] salt, byte[] digest) {
        this.username = username;
        this.time = time;
        this.salt = salt;
        this.digest = digest;
    }

    @Override
    public byte getId() {
        return 1;
    }

    public String getUsername() {
        return username;
    }

    public long getTime() {
        return time;
    }

    public byte[] getDigest() {
        return digest;
    }

    public byte[] getSalt() {
        return salt;
    }

}
