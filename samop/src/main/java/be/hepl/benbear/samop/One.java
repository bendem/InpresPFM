package be.hepl.benbear.samop;

import be.hepl.benbear.commons.protocol.Packet;

public class One implements Packet {
    public static final byte ID = 1;

    @Override
    public byte getId() {
        return ID;
    }
}
