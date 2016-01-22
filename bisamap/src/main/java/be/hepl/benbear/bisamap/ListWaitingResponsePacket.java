package be.hepl.benbear.bisamap;

import be.hepl.benbear.commons.protocol.Packet;

public class ListWaitingResponsePacket implements Packet {

    @Override
    public byte getId() {
        return PacketId.ListWaitingResponsePacket.id;
    }

}
