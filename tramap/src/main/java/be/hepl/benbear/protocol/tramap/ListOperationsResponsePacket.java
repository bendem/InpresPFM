package be.hepl.benbear.protocol.tramap;

import be.hepl.benbear.commons.protocol.Packet;

import java.util.Arrays;
import java.util.List;

public class ListOperationsResponsePacket implements Packet {

    public static final byte ID = 6;

    private final boolean ok;
    private final String reason;
    private final Movement[] movements;

    public ListOperationsResponsePacket(boolean ok, String reason, Movement[] movements) {
        this.ok = ok;
        this.reason = reason;
        this.movements = movements;
    }

    public boolean isOk() {
        return ok;
    }

    public String getReason() {
        return reason;
    }

    public List<Movement> getMovements() {
        return Arrays.asList(movements);
    }

    @Override
    public byte getId() {
        return ID;
    }
}
