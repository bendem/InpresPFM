package be.hepl.benbear.protocol.tramap;

import be.hepl.benbear.commons.serialization.BinarySerializer;
import be.hepl.benbear.commons.serialization.ObjectSerializer;

import java.time.Instant;

public class Movement {

    static {
        ObjectSerializer<Movement> serializer = new ObjectSerializer<>(Movement.class);
        BinarySerializer.getInstance().registerSerializer(
            Movement.class,
            serializer,
            serializer
        );
    }

    private final int movementId;
    private final String containerId;
    private final String destination;
    private final String company_name;
    private final Instant dateArrival;
    private final Instant dateDeparture;

    public Movement(int movementId, String containerId, String destination, String company_name, Instant dateArrival, Instant dateDeparture) {
        this.movementId = movementId;
        this.containerId = containerId;
        this.destination = destination;
        this.company_name = company_name;
        this.dateArrival = dateArrival;
        this.dateDeparture = dateDeparture;
    }

    public String getContainerId() {
        return containerId;
    }

    public String getDestination() {
        return destination;
    }

    public String getCompany_name() {
        return company_name;
    }

    public Instant getDateArrival() {
        return dateArrival;
    }

    public Instant getDateDeparture() {
        return dateDeparture;
    }

    public int getMovementId() {
        return movementId;
    }
}
