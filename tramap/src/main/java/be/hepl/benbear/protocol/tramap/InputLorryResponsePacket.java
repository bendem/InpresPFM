package be.hepl.benbear.protocol.tramap;

import java.util.Arrays;
import java.util.List;

public class InputLorryResponsePacket {

    private final boolean ok;
    private final String reason;
    private final ContainerPosition[] containers;

    public InputLorryResponsePacket(boolean ok, String reason, ContainerPosition[] containers) {
        this.ok = ok;
        this.reason = reason;
        this.containers = containers;
    }

    public boolean isOk() {
        return ok;
    }

    public String getReason() {
        return reason;
    }

    public List<ContainerPosition> getContainers() {
        return Arrays.asList(containers);
    }

}
