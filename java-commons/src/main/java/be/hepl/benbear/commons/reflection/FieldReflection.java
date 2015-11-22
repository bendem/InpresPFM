package be.hepl.benbear.commons.reflection;

import be.hepl.benbear.commons.streams.UncheckedLambda;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class FieldReflection<T> {

    private static final Consumer<Throwable> EXCEPTION_REDIRECTION = e -> {
        throw new ReflectionException(e);
    };
    public static final Predicate<Field> NON_TRANSIENT = f -> !Modifier.isTransient(f.getModifiers());
    public static final Predicate<Field> NON_SYNTHETIC = f -> !f.isSynthetic();
    public static <T> Function<Field, Object> extractFunction(T obj) {
        return UncheckedLambda.function(f -> f.get(obj), EXCEPTION_REDIRECTION);
    }

    private final Class<T> clazz;
    private final List<Field> fields;

    @SafeVarargs
    public FieldReflection(Class<T> clazz, Predicate<? super Field>... predicates) {
        this.clazz = clazz;
        this.fields = filter(Arrays.stream(clazz.getDeclaredFields()), predicates)
            .filter(f -> !Modifier.isStatic(f.getModifiers()))
            .collect(Collectors.toList());
        fields.forEach(f -> f.setAccessible(true));
    }

    public Class<T> getOwningClass() {
        return clazz;
    }

    @SafeVarargs
    public final Stream<Field> getFields(Predicate<? super Field>... predicates) {
        return filter(predicates);
    }

    @SafeVarargs
    public final Map<String, Field> getFieldMap(Predicate<? super Field>... predicates) {
        return filter(predicates).collect(Collectors.toMap(
            Field::getName,
            f -> f,
            (a, b) -> a,
            LinkedHashMap::new
        ));
    }

    @SafeVarargs
    public final Stream<Object> getValues(T obj, Predicate<? super Field>... predicates) {
        return filter(predicates)
            .map(extractFunction(obj));
    }

    @SafeVarargs
    public final Map<String, Object> getValueMap(T obj, Predicate<? super Field>... predicates) {
        return filter(predicates).collect(Collectors.toMap(
            Field::getName,
            extractFunction(obj),
            (a, b) -> a,
            LinkedHashMap::new
        ));
    }

    @SafeVarargs
    public final Stream<String> getNames(Predicate<? super Field>... predicates) {
        return filter(predicates).map(Field::getName);
    }

    @SafeVarargs
    public final Stream<Class<?>> getTypes(Predicate<? super Field>...predicates) {
        return filter(predicates).map(Field::getType);
    }

    @SafeVarargs
    public final Map<String, Class<?>> getTypeMap(Predicate<? super Field>... predicates) {
        return filter(predicates).collect(Collectors.toMap(
            Field::getName,
            Field::getType,
            (a, b) -> a,
            LinkedHashMap::new
        ));
    }

    public int count() {
        return fields.size();
    }

    @SafeVarargs
    public final long count(Predicate<? super Field>... predicates) {
        return filter(predicates).count();
    }

    @SafeVarargs
    public final FieldReflection<T> filtered(Predicate<? super Field>... predicates) {
        return new FieldReflection<>(clazz, predicates);
    }

    @SafeVarargs
    private final Stream<Field> filter(Predicate<? super Field>... predicates) {
        return filter(fields.stream(), predicates);
    }

    @SafeVarargs
    private final <T> Stream<T> filter(Stream<T> stream, Predicate<? super T>... predicates) {
        for(Predicate<? super T> predicate : predicates) {
            stream = stream.filter(predicate);
        }
        return stream;
    }

}
