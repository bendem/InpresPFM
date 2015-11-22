package be.hepl.benbear.protocol.tramap;

import be.hepl.benbear.commons.protocol.Packet;

import java.util.Arrays;
import java.util.List;

public class InputLorryWithoutReservationPacket implements Packet {

    public static final byte ID = 3;

    private final String license;
    private final String[] containerIds;

    public InputLorryWithoutReservationPacket(String license, String[] containerIds) {
        this.license = license;
        this.containerIds = containerIds;
    }

    public String getLicense() {
        return license;
    }

    public List<String> getContainerIds() {
        return Arrays.asList(containerIds);
    }

    @Override
    public byte getId() {
        return ID;
    }
}
