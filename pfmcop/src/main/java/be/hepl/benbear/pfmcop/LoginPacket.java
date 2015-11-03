package be.hepl.benbear.pfmcop;

public class LoginPacket {

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
