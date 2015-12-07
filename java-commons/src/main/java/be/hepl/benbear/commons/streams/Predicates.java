package be.hepl.benbear.commons.streams;

import java.util.function.Predicate;

public final class Predicates {

    private static final Predicate<?> NOT_NULL = x -> x != null;

    private Predicates() {}

    public static <T> Predicate<T> notNull() {
        return (Predicate<T>) NOT_NULL;
    }

}
