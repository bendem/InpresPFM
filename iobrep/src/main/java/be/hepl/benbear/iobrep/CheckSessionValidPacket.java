package be.hepl.benbear.iobrep;

import java.util.UUID;

public class CheckSessionValidPacket extends AuthenticatedPacket {

    public CheckSessionValidPacket(UUID session) {
        super(PacketId.CHECK_VALID_SESSION, session);
    }

}
