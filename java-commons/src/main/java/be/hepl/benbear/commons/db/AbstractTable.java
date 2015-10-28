package be.hepl.benbear.commons.db;

import be.hepl.benbear.commons.generics.MappedMap;
import be.hepl.benbear.commons.reflection.FieldReflection;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractTable<T> implements Table<T> {

    protected final Class<T> clazz;
    protected final String name;
    protected final Map<String, Field> primaryKeys; // name, field
    protected final FieldReflection<T> fieldReflection;

    public AbstractTable(Class<T> clazz) {
        DBTable annotation = clazz.getAnnotation(DBTable.class);
        if(annotation == null) {
            throw new IllegalArgumentException("Class '" + clazz.getName() + "' is not annotated with @" + DBTable.class.getName());
        }

        this.clazz = clazz;
        this.name = annotation.value();
        this.fieldReflection = new FieldReflection<>(clazz, FieldReflection.NON_TRANSIENT, FieldReflection.NON_SYNTHETIC);
        this.primaryKeys = Collections.unmodifiableMap(collectPrimaryKeys());
    }

    private LinkedHashMap<String, Field> collectPrimaryKeys() {
        return fieldReflection.getFields(f -> f.getAnnotation(PrimaryKey.class) != null)
            .collect(Collectors.toMap(
                f -> {
                    As as;
                    return (as = f.getAnnotation(As.class)) == null ? Mapping.transformName(f.getName()) : as.value();
                },
                f -> f,
                (a, b) -> a,
                LinkedHashMap::new
            ));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<T> getTableClass() {
        return clazz;
    }

    @Override
    public int getIdCount() {
        return primaryKeys.size();
    }

    @Override
    public Map<String, Class<?>> getIdFields() {
        return new MappedMap<>(primaryKeys, Field::getType, t -> {
            throw new UnsupportedOperationException();
        });
    }

}
