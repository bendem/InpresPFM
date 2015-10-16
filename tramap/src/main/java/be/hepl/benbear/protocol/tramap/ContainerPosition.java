package be.hepl.benbear.protocol.tramap;

import be.hepl.benbear.commons.net.BinarySerializer;
import be.hepl.benbear.commons.net.ObjectSerializer;

public class ContainerPosition {

    static {
        ObjectSerializer<ContainerPosition> serializer = new ObjectSerializer<>(ContainerPosition.class);
        BinarySerializer.getInstance().registerSerializer(
            ContainerPosition.class,
            serializer,
            serializer
        );
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
