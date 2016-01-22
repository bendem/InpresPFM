package be.hepl.benbear.bisamap;

import be.hepl.benbear.commons.protocol.Packet;

public class ComputeSalariesPacket implements Packet {

    @Override
    public byte getId() {
        return PacketId.ComputeSalariesPacket.id;
    }

}
