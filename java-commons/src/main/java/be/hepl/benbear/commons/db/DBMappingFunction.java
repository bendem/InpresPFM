package be.hepl.benbear.commons.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class DBMappingFunction {

    @FunctionalInterface
    public interface DBMapping<T> {
        T apply(ResultSet r) throws SQLException;
    }

    public static <T> Function<ResultSet, Optional<T>> unique(DBMapping<T> mapping, Consumer<SQLException> handler) {
        return r -> {
            try {
                if(r.next()) {
                    return Optional.of(mapping.apply(r));
                }
            } catch(SQLException e) {
                handler.accept(e);
            }
            return Optional.empty();
        };
    }

    public static <T> Function<ResultSet, Stream<T>> multiple(DBMapping<T> mapping, Consumer<SQLException> handler) {
        return r -> StreamSupport.stream(new Spliterator<T>() {
            @Override
            public boolean tryAdvance(Consumer<? super T> consumer) {
                try {
                    if(r.next()) {
                        consumer.accept(mapping.apply(r));
                        return true;
                    }
                } catch(SQLException e) {
                    handler.accept(e);
                }
                return false;
            }

            @Override
            public Spliterator<T> trySplit() {
                return null;
            }

            @Override
            public long estimateSize() {
                return 0;
            }

            @Override
            public int characteristics() {
                return ORDERED;
            }
        }, false);
    }
}
