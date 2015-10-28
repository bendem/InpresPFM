package be.hepl.benbear.commons.db.csv;

import be.hepl.benbear.commons.db.AbstractTable;
import be.hepl.benbear.commons.db.DBPredicate;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class CSVTable<T> extends AbstractTable<T> {

    private final CSVDatabase db;

    public CSVTable(Class<T> clazz, CSVDatabase db) {
        super(clazz);
        this.db = db;
    }

    @Override
    public CompletableFuture<Optional<T>> byId(Object... ids) {
        return null;
    }

    @Override
    public CompletableFuture<Integer> insert(T obj) {
        return null;
    }

    @Override
    public CompletableFuture<Integer> update(T obj) {
        return null;
    }

    @Override
    public CompletableFuture<Integer> deleteById(Object... ids) {
        return null;
    }

    @Override
    public CompletableFuture<Integer> delete(DBPredicate predicate) {
        return null;
    }

    @Override
    public CompletableFuture<Stream<T>> find(DBPredicate predicate) {
        return null;
    }

    @Override
    public CompletableFuture<Optional<T>> findOne(DBPredicate predicate) {
        return null;
    }

}
