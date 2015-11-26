package be.hepl.benbear.pidep;

import java.util.UUID;

public class LoginReponsePacket extends AuthenticatedPacket {

    public LoginReponsePacket(UUID session) {
        super(Id.LoginResponse, session);
    }

}
