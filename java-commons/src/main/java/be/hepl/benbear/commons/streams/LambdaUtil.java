package be.hepl.benbear.commons.streams;

import java.util.function.Consumer;

public class LambdaUtil {

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

    public static <T> Consumer<T> unchecked(ThrowingConsumer<T> t, Consumer<Throwable> handler) {
        return new WrappedConsumer<>(t, handler);
    }

}
