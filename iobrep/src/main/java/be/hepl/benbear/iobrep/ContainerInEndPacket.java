package be.hepl.benbear.iobrep;

import java.util.UUID;

public class ContainerInEndPacket extends AuthenticatedPacket {

    public ContainerInEndPacket(UUID session) {
        super(PacketId.CONTAINER_IN_END, session);
    }

}
