package be.hepl.benbear.bisamap;

import be.hepl.benbear.commons.protocol.Packet;

public class ValidateSalariesPacket implements Packet {

    @Override
    public byte getId() {
        return PacketId.ValidateSalariesPacket.id;
    }

}
