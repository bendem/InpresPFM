package be.hepl.benbear.boomap;

import be.hepl.benbear.commons.protocol.Packet;

import java.util.Arrays;
import java.util.List;

public class GetXYResponsePacket implements Packet{
    public static final byte ID = 4;

    private final boolean ok;
    private final String reason;
    private final Position[] positions;

    public GetXYResponsePacket(boolean ok, String reason, Position[] positions) {
        this.ok = ok;
        this.reason = reason;
        this.positions = positions;
    }

    @Override
    public byte getId() {
        return ID;
    }

    public boolean isOk() {
        return ok;
    }

    public String getReason() {
        return reason;
    }

    public List<Position> getPositions() {
        return Arrays.asList(positions);
    }
}
