package be.hepl.benbear.protocol.tramap;

import java.util.Collections;
import java.util.List;

public class ListOperationsResponsePacket {

    private final boolean ok;
    private final String reason;
    private final List<Movement> movements;

    public ListOperationsResponsePacket(boolean ok, String reason, List<Movement> movements) {
        this.ok = ok;
        this.reason = reason;
        this.movements = Collections.unmodifiableList(movements);
    }

    public boolean isOk() {
        return ok;
    }

    public String getReason() {
        return reason;
    }

    public List<Movement> getMovements() {
        return movements;
    }

}
