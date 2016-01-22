package be.hepl.benbear.commons.security;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public final class Cipheriscope {

    private Cipheriscope() {}

    private static final KeyGenerator KEY_GENERATOR;
    private static final Cipher CIPHER_AES;
    private static final Cipher CIPHER_RSA;
    static {
        try {
            KEY_GENERATOR = KeyGenerator.getInstance("AES");
            CIPHER_AES = Cipher.getInstance("AES_256/GCM/NoPadding");
            CIPHER_RSA = Cipher.getInstance("RSA");
        } catch(NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }

        KEY_GENERATOR.init(256);
    }

    public static SecretKey generateKey() {
        synchronized(KEY_GENERATOR) {
            return KEY_GENERATOR.generateKey();
        }
    }

    public static byte[] encrypt(SecretKey key, byte[] data) {
        return encrypt(CIPHER_AES, key, data);
    }

    public static byte[] decrypt(SecretKey key, byte[] data) {
        return decrypt(CIPHER_AES, key, data);
    }

    public static byte[] encrypt(PublicKey key, byte[] data) {
        return encrypt(CIPHER_RSA, key, data);
    }

    public static byte[] decrypt(PrivateKey key, byte[] data) {
        return decrypt(CIPHER_RSA, key, data);
    }

    private static byte[] encrypt(Cipher cipher, Key key, byte[] data) {
        synchronized(cipher) {
            try {
                cipher.init(Cipher.ENCRYPT_MODE, key);
                return cipher.doFinal(data);
            } catch(InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static byte[] decrypt(Cipher cipher, Key key, byte[] data) {
        synchronized(cipher) {
            try {
                cipher.init(Cipher.DECRYPT_MODE, key);
                return cipher.doFinal(data);
            } catch(InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
