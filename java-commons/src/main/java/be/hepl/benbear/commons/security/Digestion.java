package be.hepl.benbear.commons.security;

import be.hepl.benbear.commons.streams.UncheckedLambda;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;

public final class Digestion {

    private Digestion() {}

    private static final ThreadLocal<SecureRandom> RANDOM = ThreadLocal.withInitial(SecureRandom::new);
    private static final ThreadLocal<MessageDigest> MESSAGE_DIGEST = ThreadLocal.withInitial(UncheckedLambda.supplier(() -> MessageDigest.getInstance("sha-256")));

    public static byte[] digest(byte[] bytes) {
        return digest(ByteBuffer.wrap(bytes));
    }

    public static byte[] digest(ByteBuffer bytes) {
        MessageDigest messageDigest = MESSAGE_DIGEST.get();
        messageDigest.reset();
        messageDigest.update(bytes);
        return messageDigest.digest();
    }

    public static byte[] digest(String password, long time, byte[] salt) {
        ByteArrayOutputStream boas = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(boas);
        try {
            dos.writeLong(time);
            dos.write(salt);
            dos.write(password.getBytes());
        } catch(IOException e) {}
        return digest(boas.toByteArray());
    }

    public static byte[] salt(int size) {
        byte[] salt = new byte[size];
        RANDOM.get().nextBytes(salt);
        return salt;
    }

    public static boolean check(byte[] digest, String password, long time, byte[] salt) {
        return Arrays.equals(digest, digest(password, time, salt));
    }

}
