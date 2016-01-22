package be.hepl.benbear.bisamap;

import be.hepl.benbear.commons.protocol.Packet;

public class ListWaitingPacket implements Packet {

    @Override
    public byte getId() {
        return PacketId.ListWaitingPacket.id;
    }

}
