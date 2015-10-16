package be.hepl.benbear.protocol.tramap;

import be.hepl.benbear.commons.net.BinarySerializer;
import be.hepl.benbear.commons.net.ObjectSerializer;

public class Movement {

    static {
        ObjectSerializer<Movement> serializer = new ObjectSerializer<>(Movement.class);
        BinarySerializer.getInstance().registerSerializer(
            Movement.class,
            serializer,
            serializer
        );
    }

    // TODO No idea what should go here
    private final String containerId;
    private final String destination;

    public Movement(String containerId, String destination) {
        this.containerId = containerId;
        this.destination = destination;
    }

    public String getContainerId() {
        return containerId;
    }

    public String getDestination() {
        return destination;
    }

}
