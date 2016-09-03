package be.hepl.benbear.samop;

import be.hepl.benbear.commons.protocol.Packet;

public class All implements Packet {
    public static final byte ID = 3;

    @Override
    public byte getId() {
        return ID;
    }
}
