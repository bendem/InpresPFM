package be.hepl.benbear.protocol.tramap;

import java.util.Collections;
import java.util.List;

public class InputLorryResponsePacket {

    private final boolean ok;
    private final String reason;
    private final List<ContainerPosition> containers;

    public InputLorryResponsePacket(boolean ok, String reason, List<ContainerPosition> containers) {
        this.ok = ok;
        this.reason = reason;
        this.containers = Collections.unmodifiableList(containers);
    }

    public boolean isOk() {
        return ok;
    }

    public String getReason() {
        return reason;
    }

    public List<ContainerPosition> getContainers() {
        return containers;
    }

}
