package be.hepl.benbear.pidep;

import java.util.UUID;

public class GetContainerPerDestinationPerQuarterGraphPacket extends AuthenticatedPacket {

    private final int year;

    public GetContainerPerDestinationPerQuarterGraphPacket(UUID session, int year) {
        super(Id.GetContainerPerDestinationPerQuarterGraph, session);
        this.year = year;
    }

    public int getYear() {
        return year;
    }

}
