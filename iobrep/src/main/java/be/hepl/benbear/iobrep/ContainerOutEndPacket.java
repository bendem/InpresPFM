package be.hepl.benbear.iobrep;

import java.util.UUID;

public class ContainerOutEndPacket extends AuthenticatedPacket {

    public ContainerOutEndPacket(UUID session) {
        super(PacketId.CONTAINER_OUT_END, session);
    }

}
