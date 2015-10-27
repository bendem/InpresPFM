package be.hepl.benbear.iobrep;

import java.io.Serializable;

public abstract class Packet implements Serializable {

    private final PacketId id;

    protected Packet(PacketId id) {
        this.id = id;
    }

    public PacketId getId() {
        return id;
    }

}
