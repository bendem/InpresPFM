package be.hepl.benbear.commons.streams;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class UncheckedLambda {

    public static final Consumer<Throwable> RETHROW = t -> { throw new RuntimeException(t); };

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

    public static <T> Consumer<T> consumer(ThrowingConsumer<T> t) {
        return consumer(t, RETHROW);
    }

    public static <T> Consumer<T> consumer(ThrowingConsumer<T> t, Consumer<Throwable> handler) {
        return new WrappedConsumer<>(t, handler);
    }

    @FunctionalInterface
    public interface ThrowingPredicate<T> {
        boolean test(T t) throws Throwable;
    }

    private static class WrappedPredicate<T> implements Predicate<T> {
        private final ThrowingPredicate<T> predicate;
        private final Consumer<Throwable> handler;

        public WrappedPredicate(ThrowingPredicate<T> predicate, Consumer<Throwable> handler) {
            this.predicate = predicate;
            this.handler = handler;
        }

        @Override
        public boolean test(T t) {
            try {
                return predicate.test(t);
            } catch(Throwable e) {
                handler.accept(e);
                return false;
            }
        }
    }

    public static <T> Predicate<T> predicate(ThrowingPredicate<T> t) {
        return predicate(t, RETHROW);
    }

    public static <T> Predicate<T> predicate(ThrowingPredicate<T> t, Consumer<Throwable> handler) {
        return new WrappedPredicate<>(t, handler);
    }

    @FunctionalInterface
    public interface ThrowingRunnable {
        void run() throws Throwable;
    }

    private static class WrappedRunnable implements Runnable {
        private final ThrowingRunnable runnable;
        private final Consumer<Throwable> handler;

        public WrappedRunnable(ThrowingRunnable runnable, Consumer<Throwable> handler) {
            this.runnable = runnable;
            this.handler = handler;
        }

        @Override
        public void run() {
            try {
                runnable.run();
            } catch(Throwable e) {
                handler.accept(e);
            }
        }
    }

    public static Runnable runnable(ThrowingRunnable t) {
        return runnable(t, RETHROW);
    }

    public static Runnable runnable(ThrowingRunnable t, Consumer<Throwable> handler) {
        return new WrappedRunnable(t, handler);
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

    public static <T, U> BiConsumer<T, U> biconsumer(ThrowingBiConsumer<T, U> t) {
        return biconsumer(t, RETHROW);
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

    public static <T, R> Function<T, R> function(ThrowingFunction<T, R> t) {
        return function(t, RETHROW);
    }

    public static <T, R> Function<T, R> function(ThrowingFunction<T, R> t, Consumer<Throwable> handler) {
        return new WrappedFunction<>(t, handler);
    }

    @FunctionalInterface
    public interface ThrowingSupplier<T> {
        T get() throws Throwable;
    }

    private static class WrappedSupplier<T> implements Supplier<T> {
        private final ThrowingSupplier<T> supplier;
        private final Consumer<Throwable> handler;

        public WrappedSupplier(ThrowingSupplier<T> supplier, Consumer<Throwable> handler) {
            this.supplier = supplier;
            this.handler = handler;
        }

        @Override
        public T get() {
            try {
                return supplier.get();
            } catch(Throwable e) {
                handler.accept(e);
            }
            return null;
        }
    }

    public static <T> Supplier<T> supplier(ThrowingSupplier<T> supplier) {
        return supplier(supplier, RETHROW);
    }

    public static <T> Supplier<T> supplier(ThrowingSupplier<T> supplier, Consumer<Throwable> handler) {
        return new WrappedSupplier<>(supplier, handler);
    }

}
