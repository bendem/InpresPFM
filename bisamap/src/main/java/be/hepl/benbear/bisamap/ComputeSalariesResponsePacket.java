package be.hepl.benbear.bisamap;

import be.hepl.benbear.commons.protocol.Packet;

public class ComputeSalariesResponsePacket implements Packet {

    @Override
    public byte getId() {
        return PacketId.ComputeSalariesResponsePacket.id;
    }

}
