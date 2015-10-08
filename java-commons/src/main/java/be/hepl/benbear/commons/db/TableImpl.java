package be.hepl.benbear.commons.db;

import be.hepl.benbear.commons.generics.Tuple;

import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TableImpl<T> implements Table<T> {

    private final String name;
    private final Mapping.DBToJavaMapping<T> mapper;
    private final Database db;
    private final List<Tuple<String, Class<?>>> primaryKeys; // name, type

    protected TableImpl(Class<T> clazz, Database db) {
        DBTable annotation = clazz.getAnnotation(DBTable.class);
        if(annotation == null) {
            throw new IllegalArgumentException("Class '" + clazz.getName() + "' is not annotated with @" + DBTable.class.getName());
        }

        this.name = annotation.value();
        this.primaryKeys = Collections.unmodifiableList(collectPrimaryKeys(clazz));
        this.mapper = Mapping.createDBToJavaMapping(clazz);
        this.db = db;
    }

    private List<Tuple<String, Class<?>>> collectPrimaryKeys(Class<T> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
            .filter(f -> f.getAnnotation(PrimaryKey.class) != null)
            .<Tuple<String, Class<?>>>map(f -> {
                As as = f.getAnnotation(As.class);
                String name;
                if(as == null) {
                    name = Mapping.transformName(f.getName());
                } else {
                    name = as.value();
                }
                return new Tuple<>(name, f.getType());
            })
            .collect(Collectors.toList());
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public CompletableFuture<Optional<T>> byId(ErrorHandler handler, Object... ids) {
        if(ids.length == 0 || getIdCount() == 0 || ids.length != getIdCount()) {
            throw new IllegalArgumentException("Table has " + getIdCount() + ", got " + ids.length);
        }
        // TODO Type check ids against primaryKeys.second?

        return db.readOp(() -> {
            DBPredicate predicate = DBPredicate.of(getIdFields().get(0).first, ids[0]);
            for(int i = 1; i < ids.length; ++i) {
                predicate = predicate.and(getIdFields().get(i).first, ids[i]);
            }

            PreparedStatement stmt = db.connection.prepareStatement(
                "select * from " + name + predicate.toSql());

            for(int i = 0; i < ids.length; ++i) {
                JDBCAdapter.set(stmt, i + 1, ids[i]);
            }

            return stmt;
        }).thenApply(ResultSetAdapter.unique(mapper, handler));
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
        return find(predicate).thenApply(Stream::findFirst);
    }

    @Override
    public int getIdCount() {
        return primaryKeys.size();
    }

    @Override
    public List<Tuple<String, Class<?>>> getIdFields() {
        return primaryKeys;
    }

    @Override
    public CompletableFuture<Integer> insert(T obj) {
        List<Tuple<String, Object>> v = db.getValues(obj);
        String columns = v.stream().map(Tuple::getFirst).collect(Collectors.joining(", "));
        String values = Stream.generate(() -> "?").limit(v.size()).collect(Collectors.joining(", "));

        return db.writeOp(() -> {
            PreparedStatement stmt = db.connection.prepareStatement(
                "insert into " + name + "(" + columns + ") values (" + values + ")");

            for(int i = 0; i < v.size(); ++i) {
                JDBCAdapter.set(stmt, i + 1, v.get(i).second);
            }

            return stmt;
        });
    }

}
