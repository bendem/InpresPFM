package be.hepl.benbear.bisamap;

import be.hepl.benbear.commons.protocol.Packet;

public class GetNextBillResponsePacket implements Packet {

    private final byte[] bill;

    public GetNextBillResponsePacket(byte[] bill) {
        this.bill = bill;
    }

    public byte[] getBill() {
        return bill;
    }

    @Override
    public byte getId() {
        return PacketId.GetNextBillResponsePacket.id;
    }

}
