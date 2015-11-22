package be.hepl.benbear.containerserveradmin.proto;

import be.hepl.benbear.commons.protocol.Packet;

public class ListPacket implements Packet {

    public static final byte ID = 2;

    @Override
    public byte getId() {
        return ID;
    }

}
