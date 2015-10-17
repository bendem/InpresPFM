package be.hepl.benbear.commons.net;

import be.hepl.benbear.commons.checking.Sanity;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ObjectSerializer<T> implements Serializer<T>, Deserializer<T> {

    private final LinkedHashMap<Class<?>, Field> fields;
    private final Constructor<T> constructor;

    public ObjectSerializer(Class<T> clazz) {
        Sanity.noneNull(clazz);

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

    @Override
    public T deserialize(ByteBuffer bb) {
        Sanity.noneNull(bb);

        List<Object> args = fields.values().stream()
            .map(Field::getType)
            .map(BinarySerializer.getInstance()::getDeserializer)
            .peek(deserializer -> {
                if(deserializer == null) {
                    throw new RuntimeException("Invalid type to deserialize in "
                        + constructor.getDeclaringClass().getName());
                }
            })
            .map(deserializer -> deserializer.deserialize(bb))
            .collect(Collectors.toList());

        try {
            return constructor.newInstance(args.toArray());
        } catch(InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Failed to instantiate received packet", e); // TODO @Exception DeserializationException
        }
    }

    @Override
    public void serialize(T object, DataOutputStream dos) {
        Sanity.noneNull(object, dos);

        fields.forEach((clazz, field) -> {
            Serializer<Object> serializer = BinarySerializer.getInstance().getSerializer((Class) clazz); // TODO Remove rawtype here
            if(serializer == null) {
                throw new RuntimeException("Invalid type to serialize in "
                    + constructor.getDeclaringClass().getName());
            }

            try {
                serializer.serialize(field.get(object), dos);
            } catch(IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch(IOException e) {
                e.printStackTrace(); // TODO @Exception SerializationException?
            }
        });
    }
}
