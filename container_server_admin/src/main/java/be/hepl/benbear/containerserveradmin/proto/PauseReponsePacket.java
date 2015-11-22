package be.hepl.benbear.containerserveradmin.proto;

import be.hepl.benbear.commons.protocol.Packet;

public class PauseReponsePacket implements Packet {

    public static final byte ID = 5;

    private final String error;

    public PauseReponsePacket(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    @Override
    public byte getId() {
        return ID;
    }

}
