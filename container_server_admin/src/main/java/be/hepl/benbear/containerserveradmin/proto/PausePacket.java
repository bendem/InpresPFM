package be.hepl.benbear.containerserveradmin.proto;

import be.hepl.benbear.commons.protocol.Packet;

public class PausePacket implements Packet {

    public static final byte ID = 4;

    @Override
    public byte getId() {
        return ID;
    }

}
