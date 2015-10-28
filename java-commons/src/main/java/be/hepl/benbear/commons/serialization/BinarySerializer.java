package be.hepl.benbear.commons.serialization;

import be.hepl.benbear.commons.checking.Sanity;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

// TODO Implement Collection<?> serializer and deserializer
public class BinarySerializer {

    /**
     * This is a singleton to allow implementors to register their
     * (de)serializers at initialization time (i.e. in a static block).
     *
     * A good implementation would probably use DI, but I'm not implementing
     * that by myself for a school project (at least not until everything else
     * is working)...
     */
    private static final BinarySerializer INSTANCE = new BinarySerializer();
    public static BinarySerializer getInstance() {
        return INSTANCE;
    }

    private final Map<Class<?>, Deserializer<?>> deserializers;
    private final Map<Class<?>, Serializer<?>> serializers;

    private BinarySerializer() {
        deserializers = new HashMap<>();
        deserializers.put(byte.class, ByteBuffer::get);
        deserializers.put(short.class, ByteBuffer::getShort);
        deserializers.put(int.class, ByteBuffer::getInt);
        deserializers.put(long.class, ByteBuffer::getLong);
        // TODO Check that works against the cpp implementation
        deserializers.put(float.class, ByteBuffer::getFloat);
        // Double not implemented
        deserializers.put(String.class, bb -> {
            int len = bb.getInt();
            if(len < 0) {
                // That's what you get for using unsigned...
                return "";
            }
            byte[] bytes = new byte[len];
            bb.get(bytes);
            return new String(bytes);
        });

        serializers = new HashMap<>();
        serializers.put(byte.class, (o, dos) -> dos.writeByte((byte) o));
        serializers.put(short.class, (o, dos) -> dos.writeShort((short) o));
        serializers.put(int.class, (o, dos) -> dos.writeInt((int) o));
        serializers.put(long.class, (o, dos) -> dos.writeLong((long) o));
        // TODO Check that works against the cpp implementation
        serializers.put(long.class, (o, dos) -> dos.writeFloat((float) o));
        // Double not implemented
        serializers.put(String.class, (o, dos) -> {
            String string = (String) o;
            dos.writeInt(string.length());
            dos.write(string.getBytes());
        });

        // Register (de)serializers for array types
        Map<Class<?>, Serializer<?>> arraySerializers = new HashMap<>(serializers.size());
        Map<Class<?>, Deserializer<?>> arrayDeserializers = new HashMap<>(serializers.size());
        serializers.keySet().forEach(clazz -> {
            ArraySerializer<?> arraySerializer = new ArraySerializer(clazz);

            arraySerializers.put(arraySerializer.getArrayClass(), arraySerializer);
            arrayDeserializers.put(arraySerializer.getArrayClass(), arraySerializer);
        });
        serializers.putAll(arraySerializers);
        deserializers.putAll(arrayDeserializers);
    }

    public synchronized <T> void registerSerializer(Class<T> clazz, Serializer<T> serializer, Deserializer<T> deserializer) {
        Sanity.noneNull(clazz, serializer, deserializer);

        deserializers.put(clazz, deserializer);
        serializers.put(clazz, serializer);
    }

    public synchronized <T> Serializer<T> getSerializer(Class<T> clazz) {
        Serializer<T> serializer = (Serializer<T>) serializers.get(clazz);
        if(serializer == null) {
            // TODO @Exception Provide meaningful exceptions when no serializer
        }
        return serializer;
    }

    public synchronized <T> Deserializer<T> getDeserializer(Class<T> clazz) {
        Deserializer<T> deserializer = (Deserializer<T>) deserializers.get(clazz);
        if(deserializer == null) {
            // TODO @Exception Provide meaningful exceptions when no deserializer
        }
        return deserializer;
    }

    public <T> byte[] serialize(T object) throws IOException {
        Sanity.notNull(object, "object");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        getSerializer((Class<T>) object.getClass()).serialize(object, dos);

        return bos.toByteArray();
    }

    public <T> T deserialize(Class<T> clazz, ByteBuffer bb) {
        Sanity.noneNull(clazz, bb);

        return getDeserializer(clazz).deserialize(bb);
    }

}
