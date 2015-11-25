package be.hepl.benbear.pidep;

public class LoginPacket extends Packet {

    private final String username;
    private final byte[] digest;
    private final byte[] salt;

    public LoginPacket(String username, byte[] digest, byte[] salt) {
        super(Id.Login);
        this.username = username;
        this.digest = digest;
        this.salt = salt;
    }

    public String getUsername() {
        return username;
    }

    public byte[] getDigest() {
        return digest;
    }

    public byte[] getSalt() {
        return salt;
    }

}
