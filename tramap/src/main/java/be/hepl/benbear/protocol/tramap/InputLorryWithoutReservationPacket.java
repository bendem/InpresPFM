package be.hepl.benbear.protocol.tramap;

import java.util.Collections;
import java.util.List;

public class InputLorryWithoutReservationPacket {

    private final String license;
    private final List<String> containerIds;

    public InputLorryWithoutReservationPacket(String license, List<String> containerIds) {
        this.license = license;
        this.containerIds = Collections.unmodifiableList(containerIds);
    }

    public String getLicense() {
        return license;
    }

    public List<String> getContainerIds() {
        return containerIds;
    }

}
