package be.hepl.benbear.commons.security;

import be.hepl.benbear.commons.streams.UncheckedLambda;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.function.Supplier;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public final class Cipheriscope {

    private Cipheriscope() {}

    private static final Supplier<KeyGenerator> KEY_GENERATOR;
    private static final Supplier<Cipher> CIPHER_AES;
    private static final Supplier<Cipher> CIPHER_RSA;
    static {
        KEY_GENERATOR = UncheckedLambda.supplier(() -> {
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(256);
            return generator;
        });
        CIPHER_AES = UncheckedLambda.supplier(() -> Cipher.getInstance("AES"));
        CIPHER_RSA = UncheckedLambda.supplier(() -> Cipher.getInstance("RSA"));
    }

    public static SecretKey generateKey() {
        return KEY_GENERATOR.get().generateKey();
    }

    public static byte[] encrypt(SecretKey key, byte[] data) {
        return encrypt(CIPHER_AES.get(), key, data);
    }

    public static byte[] decrypt(SecretKey key, byte[] data) {
        return decrypt(CIPHER_AES.get(), key, data);
    }

    public static byte[] encrypt(PublicKey key, byte[] data) {
        return encrypt(CIPHER_RSA.get(), key, data);
    }

    public static byte[] decrypt(PrivateKey key, byte[] data) {
        return decrypt(CIPHER_RSA.get(), key, data);
    }

    public static boolean check(SecretKey key, byte[] data, byte[] signature) {
        return Arrays.equals(encrypt(CIPHER_AES.get(), key, data), signature);
    }

    private static byte[] encrypt(Cipher cipher, Key key, byte[] data) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(data);
        } catch(InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] decrypt(Cipher cipher, Key key, byte[] data) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(data);
        } catch(InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

}
