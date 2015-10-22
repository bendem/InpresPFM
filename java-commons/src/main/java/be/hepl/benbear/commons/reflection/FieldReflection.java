package be.hepl.benbear.commons.reflection;

import be.hepl.benbear.commons.streams.UncheckedLambda;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FieldReflection<T> {

    private static final Consumer<Throwable> EXCEPTION_REDIRECTION = e -> {
        throw new ReflectionException(e);
    };
    public static final Predicate<Field> NON_TRANSIENT = f -> !Modifier.isTransient(f.getModifiers());
    public static final Predicate<Field> NON_SYNTHETIC = f -> !f.isSynthetic();

    private final Class<T> clazz;
    private final List<Field> fields;

    public FieldReflection(Class<T> clazz) {
        this.clazz = clazz;
        this.fields = Arrays.asList(clazz.getDeclaredFields());
        fields.forEach(f -> f.setAccessible(true));
    }

    public Class<T> getOwningClass() {
        return clazz;
    }

    public Stream<Object> getValues(T obj, Predicate<Field>...predicates) {
        return filter(predicates)
            .map(UncheckedLambda.<Field, Object>function(f -> f.get(obj), EXCEPTION_REDIRECTION));
    }

    public Stream<String> getNames(Predicate<Field>...predicates) {
        return filter(predicates).map(Field::getName);
    }

    public Stream<Class<?>> getTypes(Predicate<Field>...predicates) {
        return filter(predicates).map(Field::getType);
    }

    public Map<String, Class<?>> getTypeMap(Predicate<Field>...predicates) {
        return filter(predicates).collect(Collectors.toMap(
            Field::getName,
            Field::getType,
            (a, b) -> a,
            LinkedHashMap::new
        ));
    }

    public Map<String, Object> getValueMap(T obj, Predicate<Field>...predicates) {
        return filter(predicates).collect(Collectors.toMap(
            Field::getName,
            UncheckedLambda.function(f -> f.get(obj), EXCEPTION_REDIRECTION),
            (a, b) -> a,
            LinkedHashMap::new
        ));
    }

    @SafeVarargs
    private final Stream<Field> filter(Predicate<Field>... predicates) {
        Stream<Field> stream = fields.stream();
        for(Predicate<Field> predicate : predicates) {
            stream = stream.filter(predicate);
        }
        return stream;
    }

}
