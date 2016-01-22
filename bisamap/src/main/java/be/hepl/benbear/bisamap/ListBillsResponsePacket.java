package be.hepl.benbear.bisamap;

import be.hepl.benbear.commons.protocol.Packet;

public class ListBillsResponsePacket implements Packet {

    @Override
    public byte getId() {
        return PacketId.ListBillsResponsePacket.id;
    }

}
