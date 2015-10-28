package be.hepl.benbear.commons.serialization;

import java.nio.ByteBuffer;

@FunctionalInterface
public interface Deserializer<T> {

    T deserialize(ByteBuffer bb);

}
