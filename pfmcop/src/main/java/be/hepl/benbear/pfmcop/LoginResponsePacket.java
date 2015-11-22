package be.hepl.benbear.pfmcop;

import be.hepl.benbear.commons.protocol.Packet;

public class LoginResponsePacket implements Packet {

    public static final byte ID = 2;

    private final String host;
    private final int port;

    public LoginResponsePacket(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public byte getId() {
        return ID;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
