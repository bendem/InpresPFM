package be.hepl.benbear.containerserveradmin.proto;

import be.hepl.benbear.commons.protocol.Packet;

public class StopResponsePacket implements Packet {

    public static final byte ID = 7;

    public final String error;

    public StopResponsePacket(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    @Override
    public byte getId() {
        return ID;
    }

}
