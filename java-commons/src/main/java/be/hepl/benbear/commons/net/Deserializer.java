package be.hepl.benbear.commons.net;

import java.nio.ByteBuffer;

@FunctionalInterface
public interface Deserializer<T> {

    T deserialize(ByteBuffer bb);

}
