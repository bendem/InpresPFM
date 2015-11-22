package be.hepl.benbear.protocol.tramap;

import be.hepl.benbear.commons.protocol.Packet;

import java.util.Arrays;
import java.util.List;

public class InputLorryResponsePacket implements Packet {

    public static final byte ID = 2;

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

    @Override
    public byte getId() {
        return ID;
    }
}
