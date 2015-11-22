package be.hepl.benbear.containerserveradmin.proto;

import be.hepl.benbear.commons.protocol.Packet;

public class ListResponsePacket implements Packet {

    public static final byte ID = 3;

    private final String[] ips;

    public ListResponsePacket(String[] ips) {
        this.ips = ips;
    }

    public String[] getIps() {
        return ips;
    }

    @Override
    public byte getId() {
        return ID;
    }

}
