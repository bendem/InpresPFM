package be.hepl.benbear.bisamap;

import be.hepl.benbear.commons.protocol.Packet;

public class SendBillsResponsePacket implements Packet {

    @Override
    public byte getId() {
        return PacketId.SendBillsResponsePacket.id;
    }

}
