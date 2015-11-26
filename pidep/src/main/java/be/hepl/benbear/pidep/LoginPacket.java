package be.hepl.benbear.pidep;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginPacket extends Packet {

    private static final MessageDigest MESSAGE_DIGEST;
    static {
        try {
            MESSAGE_DIGEST = MessageDigest.getInstance("sha-256");
        } catch(NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    public synchronized static byte[] digest(byte[] bytes) {
        MESSAGE_DIGEST.reset();
        MESSAGE_DIGEST.update(bytes);
        return MESSAGE_DIGEST.digest();
    }

    private final String username;
    private final long time;
    private final byte[] salt;
    private final byte[] digest;

    public LoginPacket(String username, long time, byte[] salt, byte[] digest) {
        super(Id.Login);
        this.username = username;
        this.time = time;
        this.salt = salt;
        this.digest = digest;
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
