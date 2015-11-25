package be.hepl.benbear.pidep;

import java.util.UUID;

public class LoginReponsePacket extends AuthenticatedPacket {

    private final String error;

    public LoginReponsePacket(UUID session, String error) {
        super(Id.LoginResponse, session);
        this.error = error;
    }

    public String getError() {
        return error;
    }

}
