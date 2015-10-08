package be.hepl.benbear.commons.db;

import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class CompositeIdTable<T, Id1, Id2> extends Table<T> {

    private final String idField1;
    private final String idField2;

    public CompositeIdTable(String name, DBMappingFunction.DBMapping<T> mapper, String idField1, String idField2) {
        super(name, mapper);
        this.idField1 = idField1;
        this.idField2 = idField2;
    }

    public CompletableFuture<Optional<T>> byId(Id1 id1, Id2 id2) {
        return db.readOp(() -> {
            PreparedStatement stmt = db.connection.prepareStatement(
                "select * from " + name + " where " + idField1 + " = ? and " + idField2 + " = ?");
            set(1, stmt, id1);
            set(2, stmt, id2);
            return stmt;
        }).thenApply(DBMappingFunction.unique(mapper, Throwable::printStackTrace));
    }

    @Override
    public int getIdCount() {
        return 2;
    }

    @Override
    public List<String> getIdFields() {
        return Arrays.asList(idField1, idField2);
    }

}
