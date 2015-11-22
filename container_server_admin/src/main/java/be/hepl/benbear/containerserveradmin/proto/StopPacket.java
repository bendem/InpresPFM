package be.hepl.benbear.containerserveradmin.proto;

import be.hepl.benbear.commons.protocol.Packet;

public class StopPacket implements Packet {

    public static final byte ID = 6;

    private final int time;

    public StopPacket(int time) {
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    @Override
    public byte getId() {
        return ID;
    }

}
