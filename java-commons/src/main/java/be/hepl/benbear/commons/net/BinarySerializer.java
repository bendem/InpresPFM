package be.hepl.benbear.commons.net;

import be.hepl.benbear.commons.checking.Sanity;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

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
        serializers.put(byte.class, (b, dos) -> dos.writeByte((byte) b));
        serializers.put(short.class, (b, dos) -> dos.writeShort((short) b));
        serializers.put(int.class, (b, dos) -> dos.writeInt((int) b));
        serializers.put(long.class, (b, dos) -> dos.writeLong((long) b));
        // TODO Check that works against the cpp implementation
        serializers.put(long.class, (b, dos) -> dos.writeFloat((float) b));
        // Double not implemented
        serializers.put(String.class, (b, dos) -> {
            String string = (String) b;
            dos.writeInt(string.length());
            dos.write(string.getBytes());
        });
    }

    public synchronized <T> void registerSerializer(Class<T> clazz, Serializer<T> serializer, Deserializer<T> deserializer) {
        Sanity.noneNull(clazz, serializer, deserializer);

        deserializers.put(clazz, deserializer);
        serializers.put(clazz, serializer);
    }

    public synchronized <T> Serializer<T> getSerializer(Class<T> clazz) {
        return (Serializer<T>) serializers.get(clazz);
    }

    public synchronized <T> Deserializer<T> getDeserializer(Class<T> clazz) {
        return (Deserializer<T>) deserializers.get(clazz);
    }

    public <T> byte[] serialize(T object) {
        Sanity.notNull(object, "object");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        // TODO Find out why this.<T>getSerializer doesn't work
        ((ObjectSerializer<T>) getSerializer(object.getClass())).serialize(object, dos);

        return bos.toByteArray();
    }

    public <T> T deserialize(Class<T> clazz, ByteBuffer bb) {
        Sanity.noneNull(clazz, bb);

        return getDeserializer(clazz).deserialize(bb);
    }

}
