package be.hepl.benbear.protocol.tramap;

import java.util.Collections;
import java.util.List;

public class InputLorryPacket {

    private final String reservationId;
    private final List<String> containerIds;

    public InputLorryPacket(String reservationId, List<String> containerIds) {
        this.reservationId = reservationId;
        this.containerIds = Collections.unmodifiableList(containerIds);
    }

    public String getReservationId() {
        return reservationId;
    }

    public List<String> getContainerIds() {
        return containerIds;
    }

}
