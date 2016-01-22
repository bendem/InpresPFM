package be.hepl.benbear.bisamap;

import be.hepl.benbear.commons.protocol.Packet;

public class ListBillsPacket implements Packet {

    @Override
    public byte getId() {
        return PacketId.ListBillsPacket.id;
    }

}
