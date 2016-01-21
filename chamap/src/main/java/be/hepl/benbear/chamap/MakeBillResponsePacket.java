package be.hepl.benbear.chamap;

import be.hepl.benbear.commons.protocol.Packet;

public class MakeBillResponsePacket implements Packet {

    private final String error;

    public MakeBillResponsePacket(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    @Override
    public byte getId() {
        return 4;
    }

}
