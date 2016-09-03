package be.hepl.benbear.samop;

import be.hepl.benbear.commons.protocol.Packet;

public class ListResponse implements Packet {
    public static final byte ID = 9;
    private final String[] data;

    public ListResponse(String[] data) {
        this.data = data;
    }

    public String[] getData() {
        return data;
    }

    @Override
    public byte getId() {
        return ID;
    }
}
