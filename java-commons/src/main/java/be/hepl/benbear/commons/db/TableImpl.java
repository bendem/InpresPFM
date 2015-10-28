package be.hepl.benbear.commons.db;

import be.hepl.benbear.commons.reflection.FieldReflection;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TableImpl<T> extends AbstractTable<T> {

    private final Mapping.DBToJavaMapping<T> mapper;
    private final SQLDatabase db;

    protected TableImpl(Class<T> clazz, SQLDatabase db) {
        super(clazz);
        this.mapper = Mapping.createDBToJavaMapping(fieldReflection);
        this.db = db;
    }

    @Override
    public CompletableFuture<Optional<T>> byId(Object... ids) {
        if(ids.length == 0 || getIdCount() == 0 || ids.length != getIdCount()) {
            throw new IllegalArgumentException("Table has " + getIdCount() + ", got " + ids.length);
        }

        DBPredicate predicate = null;
        int i = 0;
        for(String name : primaryKeys.keySet()) {
            if(predicate == null) {
                predicate = DBPredicate.of(name, ids[0]);
            } else {
                predicate = predicate.and(name, ids[++i]);
            }
        }

        return findOne(predicate);
    }

    @Override
    public CompletableFuture<Stream<T>> find(DBPredicate predicate) {
        String query = "select * from " + name + predicate.toSql();
        return db.readOp(() -> bind(
            db.connection.prepareStatement(query),
            predicate.values()
        )).thenApply(ResultSetAdapter.multiple(mapper, Throwable::printStackTrace)); // TODO Error handler arg
    }

    @Override
    public CompletableFuture<Optional<T>> findOne(DBPredicate predicate) {
        String query = "select * from " + name + predicate.toSql();
        return db.readOp(() -> bind(
            db.connection.prepareStatement(query),
            predicate.values()
        )).thenApply(ResultSetAdapter.unique(mapper, Throwable::printStackTrace));
    }

    @Override
    public CompletableFuture<Integer> insert(T obj) {
        Map<String, Object> v = fieldReflection.getValueMap(obj);
        String columns = v.keySet().stream().map(Mapping::transformName).collect(Collectors.joining(", "));
        String values = Stream.generate(() -> "?").limit(v.size()).collect(Collectors.joining(", "));
        String query = "insert into " + name + "(" + columns + ") values (" + values + ")";

        return db.writeOp(() -> bind(db.connection.prepareStatement(query), v.values()));
    }

    @Override
    public CompletableFuture<Integer> update(T obj) {
        DBPredicate predicate = null;
        for(Map.Entry<String, Field> entry : primaryKeys.entrySet()) {
            Object value = FieldReflection.extractFunction(obj).apply(entry.getValue());
            if(predicate == null) {
                predicate = DBPredicate.of(entry.getKey(), value);
            } else {
                predicate = predicate.and(entry.getKey(), value);
            }
        }

        if(predicate == null) {
            // TODO @Exception
            throw new RuntimeException("Couldn't build a predicate to update " + name);
        }

        Map<String, Object> updateValues = fieldReflection.getValueMap(obj);
        String sqlValues = updateValues.keySet().stream()
            .map(Mapping::transformName)
            .filter(name -> !primaryKeys.containsKey(name))
            .map(name -> name + " = ?")
            .collect(Collectors.joining(", "));

        String query = "update " + name + " set " + sqlValues + predicate.toSql();
        List<Object> values = Stream.concat(
            updateValues.entrySet().stream()
                .filter(e -> !primaryKeys.containsKey(Mapping.transformName(e.getKey())))
                .map(Map.Entry::getValue),
            predicate.values().stream()
        ).collect(Collectors.toList());

        return db.writeOp(() -> bind(db.connection.prepareStatement(query), values));
    }

    @Override
    public CompletableFuture<Integer> deleteById(Object... ids) {
        if(ids.length == 0 || getIdCount() == 0 || ids.length != getIdCount()) {
            throw new IllegalArgumentException("Table has " + getIdCount() + ", got " + ids.length);
        }

        DBPredicate predicate = null;
        int i = 0;
        for(String name : primaryKeys.keySet()) {
            if(predicate == null) {
                predicate = DBPredicate.of(name, ids[0]);
            } else {
                predicate = predicate.and(name, ids[++i]);
            }
        }

        return delete(predicate);
    }

    @Override
    public CompletableFuture<Integer> delete(DBPredicate predicate) {
        String query = "delete from " + name + predicate.toSql();
        return db.writeOp(() -> bind(db.connection.prepareStatement(query), predicate.values()));
    }

    private PreparedStatement bind(PreparedStatement stmt, Collection<Object> objects) throws SQLException {
        int i = 0;
        for(Object o : objects) {
            JDBCAdapter.set(stmt, ++i, o);
        }

        return stmt;
    }

}
