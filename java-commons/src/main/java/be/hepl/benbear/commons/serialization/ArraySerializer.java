package be.hepl.benbear.commons.serialization;

import be.hepl.benbear.commons.checking.Sanity;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;

public class ArraySerializer<T> implements Serializer<T>, Deserializer<T> {

    private final Class<?> clazz;
    private final Class<T> arrayClass;

    public ArraySerializer(Class<?> clazz) {
        Sanity.isTrue(!clazz.isArray(), "Multidimensional array serialization not supported");

        this.clazz = clazz;

        // There must be another way, no idea tho
        String internalName;
        if(clazz.isPrimitive()) {
            if(clazz == long.class) {
                // Not sure why, long is J...
                internalName = "[J";
            } else {
                internalName = "[" + Character.toUpperCase(clazz.getName().charAt(0));
            }
        } else {
            internalName = "[L" + clazz.getName() + ';';
        }

        try {
            arrayClass = (Class<T>) Class.forName(internalName);
        } catch(ClassNotFoundException e) {
            throw new RuntimeException("Invalid array class for " + clazz + ": " + internalName, e);
        }
    }

    public Class<T> getArrayClass() {
        return arrayClass;
    }

    @Override
    public T deserialize(ByteBuffer bb) {
        int length = bb.getInt(); // TODO Check for length < 0?
        if(length < 0) {
            throw new SerializationException("Array of negative size: " + length);
        }

        T array = (T) Array.newInstance(clazz, length);

        if(length == 0) {
            return array;
        }

        Deserializer<?> deserializer = BinarySerializer.getInstance().getDeserializer(clazz);
        for(int i = 0; i < length; ++i) {
            Array.set(array, i, deserializer.deserialize(bb));
        }
        return array;
    }

    @Override
    public void serialize(T obj, DataOutputStream dos) throws IOException {
        int length = Array.getLength(obj);
        dos.writeInt(length);
        Serializer serializer = BinarySerializer.getInstance().getSerializer(clazz);
        for(int i = 0; i < length; ++i) {
            serializer.serialize(Array.get(obj, i), dos);
        }
    }

}
