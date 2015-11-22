package be.hepl.benbear.protocol.tramap;

import be.hepl.benbear.commons.protocol.Packet;

public class LogoutPacket implements Packet {

    public static final byte ID = 9;

    public LogoutPacket() {
    }

    @Override
    public byte getId() {
        return ID;
    }
}
