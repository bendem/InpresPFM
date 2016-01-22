package be.hepl.benbear.bisamap;

import be.hepl.benbear.commons.protocol.Packet;

import java.util.UUID;

public class ListBillsPacket implements Packet {

    private final UUID session;
    private final int companyId;
    private final long from;
    private final long to;
    private final byte[] signature;

    public ListBillsPacket(UUID session, int companyId, long from, long to, byte[] signature) {
        this.session = session;
        this.companyId = companyId;
        this.from = from;
        this.to = to;
        this.signature = signature;
    }

    public UUID getSession() {
        return session;
    }

    public int getCompanyId() {
        return companyId;
    }

    public long getFrom() {
        return from;
    }

    public long getTo() {
        return to;
    }

    public byte[] getSignature() {
        return signature;
    }

    @Override
    public byte getId() {
        return PacketId.ListBillsPacket.id;
    }

}
