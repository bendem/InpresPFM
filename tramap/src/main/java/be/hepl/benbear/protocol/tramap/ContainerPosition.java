package be.hepl.benbear.protocol.tramap;

import be.hepl.benbear.commons.serialization.ArraySerializer;
import be.hepl.benbear.commons.serialization.BinarySerializer;
import be.hepl.benbear.commons.serialization.ObjectSerializer;

public class ContainerPosition {

    static {
        ObjectSerializer<ContainerPosition> serializer = new ObjectSerializer<>(ContainerPosition.class);
        BinarySerializer.getInstance().registerSerializer(
            ContainerPosition.class,
            serializer,
            serializer
        );
        ArraySerializer<ContainerPosition[]> arraySerializer = new ArraySerializer<>(ContainerPosition.class);
        BinarySerializer.getInstance().registerSerializer(ContainerPosition[].class, arraySerializer, arraySerializer);
    }

    private final String containerId;
    private final int x;
    private final int y;

    public ContainerPosition(String containerId, int x, int y) {
        this.containerId = containerId;
        this.x = x;
        this.y = y;
    }

    public String getContainerId() {
        return containerId;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}
