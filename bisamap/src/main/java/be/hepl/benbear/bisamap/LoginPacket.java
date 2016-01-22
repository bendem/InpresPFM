package be.hepl.benbear.bisamap;

import be.hepl.benbear.commons.protocol.Packet;

public class LoginPacket implements Packet {

    private final String username;
    private final long time;
    private final byte[] salt;
    private final byte[] digest;
    private final byte[] signKeyCiphered;
    private final byte[] cryptKeyCiphered;

    public LoginPacket(String username, long time, byte[] salt, byte[] digest, byte[] signKeyCiphered, byte[] cryptKeyCiphered) {
        this.username = username;
        this.time = time;
        this.salt = salt;
        this.digest = digest;
        this.signKeyCiphered = signKeyCiphered;
        this.cryptKeyCiphered = cryptKeyCiphered;
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

    public byte[] getSignKeyCiphered() {
        return signKeyCiphered;
    }

    public byte[] getCryptKeyCiphered() {
        return cryptKeyCiphered;
    }

    @Override
    public byte getId() {
        return PacketId.LoginPacket.id;
    }

}
