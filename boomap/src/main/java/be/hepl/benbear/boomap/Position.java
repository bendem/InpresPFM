package be.hepl.benbear.boomap;

import be.hepl.benbear.commons.serialization.ArraySerializer;
import be.hepl.benbear.commons.serialization.BinarySerializer;
import be.hepl.benbear.commons.serialization.ObjectSerializer;

public class Position {
    static {
        ObjectSerializer<Position> serializer = new ObjectSerializer<>(Position.class);
        BinarySerializer.getInstance().registerSerializer(
            Position.class,
            serializer,
            serializer
        );
        ArraySerializer<Position[]> arraySerializer = new ArraySerializer<>(Position.class);
        BinarySerializer.getInstance().registerSerializer(Position[].class, arraySerializer, arraySerializer);
    }

    private final int x;
    private final int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
