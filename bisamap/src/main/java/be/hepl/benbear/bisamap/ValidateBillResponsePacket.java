package be.hepl.benbear.bisamap;

import be.hepl.benbear.commons.protocol.Packet;

public class ValidateBillResponsePacket implements Packet {

    @Override
    public byte getId() {
        return PacketId.ValidateBillResponsePacket.id;
    }

}
