package be.hepl.benbear.pfmcop;

public class LoginPacket {

    private final String username;
    private final char[] digest;

    public LoginPacket(String username, char[] digest) {
        this.username = username;
        this.digest = digest;
    }

    public String getUsername() {
        return username;
    }

    public char[] getDigest() {
        return digest;
    }

}
