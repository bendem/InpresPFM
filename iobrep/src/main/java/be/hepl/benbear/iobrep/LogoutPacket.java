package be.hepl.benbear.iobrep;

import java.util.UUID;

public class LogoutPacket extends AuthenticatedPacket {

    public LogoutPacket(UUID session) {
        super(PacketId.LOGOUT, session);
    }

}
