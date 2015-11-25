package be.hepl.benbear.pidep;

import java.util.UUID;

public class GetContainerPerDestinationGraphPacket extends AuthenticatedPacket {

    public enum Type {
        MONTHLY, YEARLY
    }

    private final Type type;
    private final int value;

    public GetContainerPerDestinationGraphPacket(UUID session, Type type, int value) {
        super(Id.GetContainerPerDestinationGraph, session);
        this.type = type;
        this.value = value;
    }

    public Type getType() {
        return type;
    }

    public int getValue() {
        return value;
    }

}
