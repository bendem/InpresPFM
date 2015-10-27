package be.hepl.benbear.iobrep;

import java.util.UUID;

public class GetContainersPacket extends AuthenticatedPacket {

    private final String destination;
    private final Criteria criteria;

    public GetContainersPacket(UUID session, String destination, Criteria criteria) {
        super(PacketId.GET_CONTAINERS, session);
        this.destination = destination;
        this.criteria = criteria;
    }

    public String getDestination() {
        return destination;
    }

    public Criteria getCriteria() {
        return criteria;
    }

}
