package be.hepl.benbear.protocol.tramap;

import java.util.Arrays;
import java.util.List;

public class InputLorryWithoutReservationPacket {

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

}
