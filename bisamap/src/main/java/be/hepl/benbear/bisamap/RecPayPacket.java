package be.hepl.benbear.bisamap;

import be.hepl.benbear.commons.protocol.Packet;

public class RecPayPacket implements Packet {

    @Override
    public byte getId() {
        return PacketId.RecPayPacket.id;
    }

}
