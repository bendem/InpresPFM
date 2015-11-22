package be.hepl.benbear.pfmcop;

public class LoginPacket {

    public static byte digest(String password) {
        int sum = 0;
        for(int i = 0; i < password.length(); sum += password.charAt(i), ++i);
        return (byte) (sum % 67);
    }

    private final String username;
    private final byte digest;

    public LoginPacket(String username, byte digest) {
        this.username = username;
        this.digest = digest;
    }

    public String getUsername() {
        return username;
    }

    public byte getDigest() {
        return digest;
    }

}
