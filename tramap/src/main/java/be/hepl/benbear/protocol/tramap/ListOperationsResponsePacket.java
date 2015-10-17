package be.hepl.benbear.protocol.tramap;

import java.util.Arrays;
import java.util.List;

public class ListOperationsResponsePacket {

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

}
