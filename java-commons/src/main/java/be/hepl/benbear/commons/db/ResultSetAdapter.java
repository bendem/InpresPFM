package be.hepl.benbear.commons.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/* package */ class ResultSetAdapter {

    /**
     * Retrieves a single element from a ResultSet and maps it to a java object
     * using the provided mapping function.
     *
     * @param mapping a mapping function to convert the ResultSet row
     * @param handler a sql error handler
     * @param <T> the type of the expected object
     * @return a function able to map a sql row to a java object
     */
    public static <T> Function<ResultSet, Optional<T>> unique(Mapping.DBToJavaMapping<T> mapping, ErrorHandler handler) {
        return r -> {
            try {
                if(r.next()) {
                    return Optional.of(mapping.apply(r));
                }
            } catch(SQLException e) {
                handler.accept(e);
            } finally {
                try {
                    r.close();
                    r.getStatement().close();
                } catch(SQLException e) {
                    handler.accept(e);
                }
            }

            return Optional.empty();
        };
    }

    /**
     * Lazily retrieves multiple elements from a ResultSet and maps them to java
     * objects using the provided mapping function.
     *
     * @param mapping a mapping function to convert a ResultSet row
     * @param handler a sql error handler
     * @param <T> the type of the expected objects
     * @return a function able to map a sql row to a stream of java object
     */
    public static <T> Function<ResultSet, Stream<T>> multiple(Mapping.DBToJavaMapping<T> mapping, ErrorHandler handler) {
        return r -> StreamSupport.stream(new Spliterator<T>() {
            @Override
            public boolean tryAdvance(Consumer<? super T> consumer) {
                try {
                    if(r.next()) {
                        consumer.accept(mapping.apply(r));
                        return true;
                    }
                    r.getStatement().close();
                    r.close();
                } catch(SQLException e) {
                    handler.accept(e);
                }
                return false;
            }

            @Override
            public Spliterator<T> trySplit() {
                // Not supported
                return null;
            }

            @Override
            public long estimateSize() {
                // Not supported
                return Long.MAX_VALUE;
            }

            @Override
            public int characteristics() {
                return ORDERED;
            }
        }, false);
    }
}
