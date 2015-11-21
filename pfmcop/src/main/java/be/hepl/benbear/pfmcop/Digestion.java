package be.hepl.benbear.pfmcop;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class Digestion {

    private Digestion() {}

    private static final MessageDigest MESSAGE_DIGEST;
    static {
        try {
            MESSAGE_DIGEST = MessageDigest.getInstance("sha-1");
        } catch(NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized static byte[] digest(ByteBuffer bytes) {
        MESSAGE_DIGEST.reset();
        MESSAGE_DIGEST.update(bytes);
        return MESSAGE_DIGEST.digest();
    }

}
