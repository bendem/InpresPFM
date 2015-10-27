package be.hepl.benbear.iobrep;

import java.util.UUID;

public class ContainerOutPacket extends AuthenticatedPacket {

    private final String containerId;

    public ContainerOutPacket(UUID session, String containerId) {
        super(PacketId.CONTAINER_OUT, session);
        this.containerId = containerId;
    }

    public String getContainerId() {
        return containerId;
    }

}
