package be.hepl.benbear.commons.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class SimpleTable<T, Id> extends Table<T> {

    private final String idField;

    protected SimpleTable(String name, DBMappingFunction.DBMapping<T> mapper, String idField) {
        super(name, mapper);
        this.idField = idField;
    }

    public CompletableFuture<Optional<T>> byId(Id id, Consumer<SQLException> handler) {
        return db.readOp(() -> {
            PreparedStatement stmt = db.connection.prepareStatement(
                "select * from " + name + " where " + idField + " = ?");
            set(1, stmt, id);
            return stmt;
        }).thenApply(new DBMappingFunction<>(mapper, Throwable::printStackTrace));
    }

    @Override
    public int getIdCount() {
        return 1;
    }

    @Override
    public List<String> getIdFields() {
        return Collections.singletonList(idField);
    }
}
