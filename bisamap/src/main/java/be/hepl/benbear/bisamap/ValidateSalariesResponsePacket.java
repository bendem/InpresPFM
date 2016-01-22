package be.hepl.benbear.bisamap;

import be.hepl.benbear.commons.protocol.Packet;

public class ValidateSalariesResponsePacket implements Packet {

    @Override
    public byte getId() {
        return PacketId.ValidateSalariesResponsePacket.id;
    }

}
