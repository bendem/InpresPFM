package be.hepl.benbear.iobrep;

import java.util.UUID;

public class ContainerInPacket extends AuthenticatedPacket {

    private final Container container;

    public ContainerInPacket(UUID session, Container container) {
        super(PacketId.CONTAINER_IN, session);
        this.container = container;
    }

    public Container getContainer() {
        return container;
    }

}
