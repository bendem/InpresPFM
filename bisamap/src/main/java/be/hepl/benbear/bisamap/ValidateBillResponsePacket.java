package be.hepl.benbear.bisamap;

import be.hepl.benbear.commons.protocol.Packet;

public class ValidateBillResponsePacket implements Packet {

    private final boolean wasValid;

    public ValidateBillResponsePacket(boolean wasValid) {
        this.wasValid = wasValid;
    }

    public boolean wasValid() {
        return wasValid;
    }

    @Override
    public byte getId() {
        return PacketId.ValidateBillResponsePacket.id;
    }

}
