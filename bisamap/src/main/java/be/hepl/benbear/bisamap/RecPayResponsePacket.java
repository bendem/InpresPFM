package be.hepl.benbear.bisamap;

import be.hepl.benbear.commons.protocol.Packet;

public class RecPayResponsePacket implements Packet {

    @Override
    public byte getId() {
        return PacketId.RecPayResponsePacket.id;
    }

}
