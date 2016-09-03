package be.hepl.benbear.samop;

import be.hepl.benbear.commons.protocol.Packet;

public class List implements Packet {
    public static final byte ID = 8;

    private final int month;

    public List(int month) {
        this.month = month;
    }

    public int getMonth() {
        return month;
    }

    @Override
    public byte getId() {
        return ID;
    }
}
