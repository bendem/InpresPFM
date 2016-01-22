package be.hepl.benbear.bisamap;

import be.hepl.benbear.commons.protocol.Packet;

public class SendBillsPacket implements Packet {

    @Override
    public byte getId() {
        return PacketId.SendBillsPacket.id;
    }

}
