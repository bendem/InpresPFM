package be.hepl.benbear.protocol.tramap;

import be.hepl.benbear.commons.serialization.ArraySerializer;
import be.hepl.benbear.commons.serialization.BinarySerializer;
import be.hepl.benbear.commons.serialization.ObjectSerializer;

public class Movement {

    static {
        ObjectSerializer<Movement> serializer = new ObjectSerializer<>(Movement.class);
        BinarySerializer.getInstance().registerSerializer(
            Movement.class,
            serializer,
            serializer
        );
        ArraySerializer<Movement[]> arraySerializer = new ArraySerializer<>(Movement.class);
        BinarySerializer.getInstance().registerSerializer(Movement[].class, arraySerializer, arraySerializer);
    }

    private final int movementId;
    private final String containerId;
    private final String destination;
    private final String companyName;
    private final long dateArrival;
    private final long dateDeparture;

    public Movement(int movementId, String containerId, String destination, String companyName, long dateArrival, long dateDeparture) {
        this.movementId = movementId;
        this.containerId = containerId;
        this.destination = destination;
        this.companyName = companyName;
        this.dateArrival = dateArrival;
        this.dateDeparture = dateDeparture;
    }

    public String getContainerId() {
        return containerId;
    }

    public String getDestination() {
        return destination;
    }

    public String getCompanyName() {
        return companyName;
    }

    public long getDateArrival() {
        return dateArrival;
    }

    public long getDateDeparture() {
        return dateDeparture;
    }

    public int getMovementId() {
        return movementId;
    }
}
