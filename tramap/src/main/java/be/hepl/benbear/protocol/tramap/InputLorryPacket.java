package be.hepl.benbear.protocol.tramap;

import java.util.Arrays;
import java.util.List;

public class InputLorryPacket {

    private final String reservationId;
    private final String[] containerIds;

    public InputLorryPacket(String reservationId, String[] containerIds) {
        this.reservationId = reservationId;
        this.containerIds = containerIds;
    }

    public String getReservationId() {
        return reservationId;
    }

    public List<String> getContainerIds() {
        return Arrays.asList(containerIds);
    }

}
