package be.hepl.benbear.commons.streams;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class UncheckedLambda {

    @FunctionalInterface
    public interface ThrowingConsumer<T> {
        void accept(T t) throws Throwable;
    }

    private static class WrappedConsumer<T> implements Consumer<T> {
        private final ThrowingConsumer<T> consumer;
        private final Consumer<Throwable> handler;

        public WrappedConsumer(ThrowingConsumer<T> consumer, Consumer<Throwable> handler) {
            this.consumer = consumer;
            this.handler = handler;
        }

        @Override
        public void accept(T t) {
            try {
                consumer.accept(t);
            } catch(Throwable e) {
                handler.accept(e);
            }
        }
    }

    public static <T> Consumer<T> consumer(ThrowingConsumer<T> t, Consumer<Throwable> handler) {
        return new WrappedConsumer<>(t, handler);
    }

    @FunctionalInterface
    public interface ThrowingBiConsumer<T, U> {
        void accept(T t, U u) throws Throwable;
    }

    private static class WrappedBiConsumer<T, U> implements BiConsumer<T, U> {
        private final ThrowingBiConsumer<T, U> consumer;
        private final Consumer<Throwable> handler;

        public WrappedBiConsumer(ThrowingBiConsumer<T, U> consumer, Consumer<Throwable> handler) {
            this.consumer = consumer;
            this.handler = handler;
        }

        @Override
        public void accept(T t, U u) {
            try {
                consumer.accept(t, u);
            } catch(Throwable e) {
                handler.accept(e);
            }
        }
    }

    public static <T, U> BiConsumer<T, U> biconsumer(ThrowingBiConsumer<T, U> t, Consumer<Throwable> handler) {
        return new WrappedBiConsumer<>(t, handler);
    }

    @FunctionalInterface
    public interface ThrowingFunction<T, R> {
        R apply(T t) throws Throwable;
    }

    private static class WrappedFunction<T, R> implements Function<T, R> {
        private final ThrowingFunction<T, R> function;
        private final Consumer<Throwable> handler;

        public WrappedFunction(ThrowingFunction<T, R> function, Consumer<Throwable> handler) {
            this.function = function;
            this.handler = handler;
        }

        @Override
        public R apply(T t) {
            try {
                return function.apply(t);
            } catch(Throwable e) {
                handler.accept(e);
            }
            return null;
        }
    }

    public static <T, R> Function<T, R> function(ThrowingFunction<T, R> t, Consumer<Throwable> handler) {
        return new WrappedFunction<>(t, handler);
    }

}
