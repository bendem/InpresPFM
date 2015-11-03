package be.hepl.benbear.pfmcop;

public class LoginResponsePacket {

    private final String host;
    private final int port;

    public LoginResponsePacket(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

}
