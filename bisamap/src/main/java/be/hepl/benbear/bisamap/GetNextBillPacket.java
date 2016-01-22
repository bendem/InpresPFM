package be.hepl.benbear.bisamap;

import be.hepl.benbear.commons.protocol.Packet;

import java.util.UUID;

public class GetNextBillPacket implements Packet {

    private final UUID session;

    public GetNextBillPacket(UUID session) {
        this.session = session;
    }

    public UUID getSession() {
        return session;
    }

    @Override
    public byte getId() {
        return PacketId.GetNextBillPacket.id;
    }

}
