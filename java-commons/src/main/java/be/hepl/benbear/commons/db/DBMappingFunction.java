package be.hepl.benbear.commons.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class DBMappingFunction<T> implements Function<ResultSet, Optional<T>> {

    @FunctionalInterface
    public interface DBMapping<T> {
        T apply(ResultSet r) throws SQLException;
    }

    private final DBMapping<T> mapping;
    private final Consumer<SQLException> handler;

    public DBMappingFunction(DBMapping<T> mapping, Consumer<SQLException> handler) {
        this.mapping = mapping;
        this.handler = handler;
    }

    @Override
    public Optional<T> apply(ResultSet r) {
        try {
            if(r.next()) {
                return Optional.of(mapping.apply(r));
            }
        } catch(SQLException e) {
            handler.accept(e);
        }
        return Optional.empty();
    }
}
