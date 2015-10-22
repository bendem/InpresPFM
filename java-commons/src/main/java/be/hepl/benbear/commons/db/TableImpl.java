package be.hepl.benbear.commons.db;

import be.hepl.benbear.commons.generics.MappedMap;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TableImpl<T> implements Table<T> {

    private final Class<T> clazz;
    private final String name;
    private final Mapping.DBToJavaMapping<T> mapper;
    private final Database db;
    private final Map<String, Field> primaryKeys; // name, field

    protected TableImpl(Class<T> clazz, Database db) {
        DBTable annotation = clazz.getAnnotation(DBTable.class);
        if(annotation == null) {
            throw new IllegalArgumentException("Class '" + clazz.getName() + "' is not annotated with @" + DBTable.class.getName());
        }

        this.clazz = clazz;
        this.name = annotation.value();
        this.primaryKeys = Collections.unmodifiableMap(collectPrimaryKeys(clazz));
        this.mapper = Mapping.createDBToJavaMapping(clazz);
        this.db = db;
    }

    private LinkedHashMap<String, Field> collectPrimaryKeys(Class<T> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
            .filter(f -> f.getAnnotation(PrimaryKey.class) != null)
            .collect(Collectors.toMap(
                f -> {
                    As as;
                    return (as = f.getAnnotation(As.class)) == null ? Mapping.transformName(f.getName()) : as.value();
                },
                f -> f,
                (a, b) -> a,
                LinkedHashMap::new
            ));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<T> getTableClass() {
        return clazz;
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
    public CompletableFuture<Stream<T>> find() {
        return find(DBPredicate.empty());
    }

    @Override
    public CompletableFuture<Stream<T>> find(DBPredicate predicate) {
        String query = "select * from " + name + predicate.toSql();
        return db.readOp(() -> bind(
            db.connection.prepareStatement(query),
            predicate.values())
        ).thenApply(ResultSetAdapter.multiple(mapper, Throwable::printStackTrace)); // TODO Error handler arg
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
    public int getIdCount() {
        return primaryKeys.size();
    }

    @Override
    public Map<String, Class<?>> getIdFields() {
        return new MappedMap<>(primaryKeys, Field::getType, t -> {
            throw new UnsupportedOperationException();
        });
    }

    @Override
    public CompletableFuture<Integer> insert(T obj) {
        LinkedHashMap<String, Object> v = db.getValues(obj);
        String columns = v.keySet().stream().collect(Collectors.joining(", "));
        String values = Stream.generate(() -> "?").limit(v.size()).collect(Collectors.joining(", "));
        String query = "insert into " + name + "(" + columns + ") values (" + values + ")";

        return db.writeOp(() -> bind(db.connection.prepareStatement(query), v.values()));
    }

    @Override
    public CompletableFuture<Integer> update(T obj) {
        DBPredicate predicate = null;
        for(Map.Entry<String, Field> entry : primaryKeys.entrySet()) {
            Object value;
            try {
                value = entry.getValue().get(obj);
            } catch(IllegalAccessException e) {
                throw new RuntimeException(e);
            }
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

        LinkedHashMap<String, Object> values = db.getValues(obj);
        String sqlValues = values.keySet().stream()
            .filter(name -> !primaryKeys.containsKey(name))
            .map(name -> name + " = ?")
            .collect(Collectors.joining(", "));

        String query = "update " + name + " set " + sqlValues + predicate.toSql();
        return db.writeOp(() -> bind(db.connection.prepareStatement(query), values.values()));
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
