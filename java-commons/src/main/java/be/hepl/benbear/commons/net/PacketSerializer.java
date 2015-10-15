package be.hepl.benbear.commons.net;

import be.hepl.benbear.commons.streams.UncheckedLambda;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/* package */ class PacketSerializer<T> {

    private static final Map<Class<?>, Function<ByteBuffer, ?>> PRIMITIVE_READING;
    private static final Map<Class<?>, BiConsumer<Object, DataOutputStream>> PRIMITIVE_WRITING;

    private static Consumer<Throwable> dosException(String type) {
        return e -> {
            throw new RuntimeException("Could not write a " + type + " into the DataOutputStream", e);
        };
    }
    static {
        Map<Class<?>, Function<ByteBuffer, ?>> readMap = new HashMap<>();
        readMap.put(byte.class, ByteBuffer::get);
        readMap.put(short.class, ByteBuffer::getShort);
        readMap.put(int.class, ByteBuffer::getInt);
        readMap.put(long.class, ByteBuffer::getLong);
        readMap.put(float.class, ByteBuffer::getFloat);
        // Double not implemented
        readMap.put(String.class, bb -> {
            int len = bb.getInt();
            if(len < 0) {
                // That's what you get for using unsigned...
                return "";
            }
            byte[] bytes = new byte[len];
            bb.get(bytes);
            return new String(bytes);
        });
        PRIMITIVE_READING = Collections.unmodifiableMap(readMap);

        Map<Class<?>, BiConsumer<Object, DataOutputStream>> writeMap = new HashMap<>();
        writeMap.put(byte.class, UncheckedLambda.biconsumer((b, dos) -> dos.writeByte((byte) b), dosException("byte")));
        writeMap.put(short.class, UncheckedLambda.biconsumer((b, dos) -> dos.writeShort((short) b), dosException("short")));
        writeMap.put(int.class, UncheckedLambda.biconsumer((b, dos) -> dos.writeInt((int) b), dosException("int")));
        writeMap.put(long.class, UncheckedLambda.biconsumer((b, dos) -> dos.writeLong((long) b), dosException("long")));
        writeMap.put(long.class, UncheckedLambda.biconsumer((b, dos) -> dos.writeFloat((float) b), dosException("float")));
        // Double not implemented
        writeMap.put(String.class, UncheckedLambda.biconsumer((b, dos) -> {
            String string = (String) b;
            dos.writeInt(string.length());
            dos.write(string.getBytes());
        }, dosException("String")));
        PRIMITIVE_WRITING = Collections.unmodifiableMap(writeMap);
    }

    private final LinkedHashMap<Class<?>, Field> fields;
    private final Constructor<T> constructor;

    public PacketSerializer(Class<T> clazz) {
        this.fields = collectFields(clazz);
        try {
            this.constructor = clazz.getConstructor(fields.keySet().toArray(new Class<?>[fields.size()]));
        } catch(NoSuchMethodException e) {
            throw new RuntimeException("No constructor to build the object from each of its fields", e);
        }
    }

    private LinkedHashMap<Class<?>, Field> collectFields(Class<T> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
            .filter(f -> !Modifier.isTransient(f.getModifiers()))
            .filter(f -> !f.isSynthetic())
            .collect(Collectors.toMap(
                Field::getType,
                Function.identity(),
                (a, b) -> {
                    throw new IllegalStateException(String.format("Duplicate key %s", new Object[]{a}));
                },
                LinkedHashMap::new
            ));
    }

    public T deserialize(ByteBuffer bb) {
        List<Object> args = fields.values().stream()
            .map(PRIMITIVE_READING::get)
            .peek(mapper -> {
                if(mapper == null) {
                    throw new RuntimeException("Invalid type to deserialize in "
                        + constructor.getDeclaringClass().getName());
                }
            })
            .map(mapper -> mapper.apply(bb))
            .collect(Collectors.toList());

        try {
            return constructor.newInstance(args.toArray());
        } catch(InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Failed to instantiate received packet", e);
        }
    }

    public byte[] serialize(T packet) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(fields.size() * 4);// Pure guess
        DataOutputStream dos = new DataOutputStream(bos);
        fields.forEach((clazz, field) -> {
            BiConsumer<Object, DataOutputStream> dosBiConsumer = PRIMITIVE_WRITING.get(clazz);
            if(dosBiConsumer == null) {
                throw new RuntimeException("Invalid type to serialize in "
                    + constructor.getDeclaringClass().getName());
            }
            try {
                dosBiConsumer.accept(field.get(packet), dos);
            } catch(IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
        return bos.toByteArray();
    }
}
