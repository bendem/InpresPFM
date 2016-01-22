package be.hepl.benbear.bisamap;

import be.hepl.benbear.commons.protocol.Packet;

import java.util.UUID;

public class ValidateBillPacket implements Packet {

    private final UUID session;
    private final int billId;
    private final byte[] signature;

    public ValidateBillPacket(UUID session, int billId, byte[] signature) {
        this.session = session;
        this.billId = billId;
        this.signature = signature;
    }

    public UUID getSession() {
        return session;
    }

    public int getBillId() {
        return billId;
    }

    public byte[] getSignature() {
        return signature;
    }

    @Override
    public byte getId() {
        return PacketId.ValidateBillPacket.id;
    }

}
