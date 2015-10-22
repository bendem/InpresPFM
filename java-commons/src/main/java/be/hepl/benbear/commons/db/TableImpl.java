package be.hepl.benbear.commons.db;

import be.hepl.benbear.commons.generics.MappedMap;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    public CompletableFuture<Optional<T>> byId(ErrorHandler handler, Object... ids) {
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
        return db.readOp(() -> {
            PreparedStatement stmt = db.connection.prepareStatement("select * from " + name + predicate.toSql());
            List<Object> values = predicate.values();
            for(int i = 0; i < values.size(); ++i) {
                JDBCAdapter.set(stmt, i + 1, values.get(i));
            }

            return stmt;
        }).thenApply(ResultSetAdapter.multiple(mapper, Throwable::printStackTrace)); // TODO Error handler arg
    }

    @Override
    public CompletableFuture<Optional<T>> findOne(DBPredicate predicate) {
        return db.readOp(() -> {
            PreparedStatement stmt = db.connection.prepareStatement(
                "select * from " + name + predicate.toSql());

            List<Object> values = predicate.values();
            for(int i = 0; i < values.size(); i++) {
                JDBCAdapter.set(stmt, i + 1, values.get(i));
            }

            return stmt;
        }).thenApply(ResultSetAdapter.unique(mapper, Throwable::printStackTrace));
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

        return db.writeOp(() -> {
            PreparedStatement stmt = db.connection.prepareStatement(
                "insert into " + name + "(" + columns + ") values (" + values + ")");

            int i = 0;
            for(Object o : v.values()) {
                JDBCAdapter.set(stmt, ++i, o);
            }

            return stmt;
        });
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

        return db.writeOp(() -> {
            PreparedStatement stmt = db.connection.prepareStatement(query);

            int i = 0;
            for(Object o : values.values()) {
                JDBCAdapter.set(stmt, ++i, o);
            }

            return stmt;
        });
    }

}
