package be.hepl.benbear.commons.net;

import java.io.DataOutputStream;
import java.io.IOException;

@FunctionalInterface
public interface Serializer<T> {

    void serialize(T obj, DataOutputStream dos) throws IOException;

}
