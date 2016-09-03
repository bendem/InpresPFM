package be.hepl.benbear.samop;

import be.hepl.benbear.commons.protocol.Packet;

public class OneResponse implements Packet {
    public static final byte ID = 2;

    @Override
    public byte getId() {
        return ID;
    }
}
