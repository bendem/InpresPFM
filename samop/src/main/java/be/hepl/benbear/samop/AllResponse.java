package be.hepl.benbear.samop;

import be.hepl.benbear.commons.protocol.Packet;

public class AllResponse implements Packet {
    public static final byte ID = 4;

    @Override
    public byte getId() {
        return ID;
    }
}
