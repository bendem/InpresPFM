package be.hepl.benbear.protocol.tramap;

import be.hepl.benbear.commons.protocol.Packet;

import java.util.Arrays;
import java.util.List;

public class InputLorryPacket implements Packet {

    public static final byte ID = 1;

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

    @Override
    public byte getId() {
        return ID;
    }
}
