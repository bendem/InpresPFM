package be.hepl.benbear.bisamap;

import be.hepl.benbear.commons.protocol.Packet;

public class ListBillsResponsePacket implements Packet {

    private final byte[] bills;

    public ListBillsResponsePacket(byte[] bills) {
        this.bills = bills;
    }

    public byte[] getBills() {
        return bills;
    }

    @Override
    public byte getId() {
        return PacketId.ListBillsResponsePacket.id;
    }

}
