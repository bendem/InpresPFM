package be.hepl.benbear.iobrep;

public class LoginPacket extends Packet {

    private final String username;
    private final String password;

    public LoginPacket(String username, String password) {
        super(PacketId.LOGIN);
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
