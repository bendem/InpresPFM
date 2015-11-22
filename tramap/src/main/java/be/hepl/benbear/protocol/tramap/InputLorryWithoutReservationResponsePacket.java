package be.hepl.benbear.protocol.tramap;

import be.hepl.benbear.commons.protocol.Packet;

public class InputLorryWithoutReservationResponsePacket extends InputResultPacket implements Packet {

    public static final byte ID = 4;

    public InputLorryWithoutReservationResponsePacket(boolean ok, String reason, ContainerPosition[] containers) {
        super(ok, reason, containers);
    }

    @Override
    public byte getId() {
        return ID;
    }
}
