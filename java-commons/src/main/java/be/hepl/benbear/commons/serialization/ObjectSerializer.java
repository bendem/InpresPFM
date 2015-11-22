package be.hepl.benbear.commons.serialization;

import be.hepl.benbear.commons.checking.Sanity;
import be.hepl.benbear.commons.logging.Log;
import be.hepl.benbear.commons.reflection.FieldReflection;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.stream.Collectors;

public class ObjectSerializer<T> implements Serializer<T>, Deserializer<T> {

    private final FieldReflection<T> fieldReflection;
    private final Constructor<T> constructor;

    public ObjectSerializer(Class<T> clazz) {
        Sanity.noneNull(clazz);

        this.fieldReflection = new FieldReflection<>(clazz, FieldReflection.NON_TRANSIENT, FieldReflection.NON_SYNTHETIC);
        try {
            this.constructor = clazz.getConstructor(fieldReflection.getTypes().toArray(Class<?>[]::new));
        } catch(NoSuchMethodException e) {
            throw new RuntimeException("No constructor to build the object from each of its fields", e);
        }
    }

    @Override
    public T deserialize(ByteBuffer bb) {
        Sanity.noneNull(bb);

        List<Object> args = fieldReflection.getTypes()
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

        fieldReflection.getFields().forEach(f -> {
            Serializer<Object> serializer = BinarySerializer.getInstance().getSerializer((Class) f.getType()); // TODO Remove rawtype here
            if(serializer == null) {
                throw new RuntimeException("Invalid type to serialize in "
                    + constructor.getDeclaringClass().getName());
            }

            try {
                Log.d("Serializing %s from %s", f, object);
                serializer.serialize(FieldReflection.extractFunction(object).apply(f), dos);
            } catch(IOException e) {
                Log.e("Failed to serialize %s", e, object); // TODO @Exception SerializationException?
            }
        });
    }

}
