package be.hepl.benbear.protocol.tramap;

public class LoginPacket {

    private final String username;
    private final String password;

    public LoginPacket(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}
