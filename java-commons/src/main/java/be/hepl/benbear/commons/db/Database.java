package be.hepl.benbear.commons.db;

import be.hepl.benbear.commons.generics.Tuple;
import be.hepl.benbear.commons.streams.UncheckedLambda;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Database implements AutoCloseable {

    // I'm against this, a thread pool would make much more sense
    // but they made me do it...
    private final Tuple<Thread, DbRunnable<ResultSet>> readWorker;
    private final Tuple<Thread, DbRunnable<Integer>> writeWorker;
    private final Map<Class<?>, Table<?>> tables;
    /* package */ Connection connection;

    // Someone told me this should be a bean, so there you go, empty constructor
    public Database() {
        DbRunnable<ResultSet> read = new DbRunnable<>();
        readWorker = new Tuple<>(new Thread(read), read);

        DbRunnable<Integer> write = new DbRunnable<>();
        writeWorker = new Tuple<>(new Thread(write), write);

        tables = new ConcurrentHashMap<>();
    }

    public Database connect(String jdbc, String username, String password) {
        if(isConnected()) {
            throw new IllegalStateException("Already connected");
        }

        try {
            connection = DriverManager.getConnection(jdbc, username, password);
        } catch(SQLException e) {
            throw new RuntimeException(e);
        }

        readWorker.first.start();
        writeWorker.first.start();

        return this;
    }

    public <T> Database registerClass(Class<T> clazz) {
        DBTable annotation = clazz.getAnnotation(DBTable.class);

        TableImpl<T> table = new TableImpl<>(clazz, createMapper(clazz), this);
        tables.put(clazz, table);

        return this;
    }

    // TODO Move that to its own class
    private <T> DBMappingFunction.DBMapping<T> createMapper(Class<T> clazz) {
        List<Field> fields = Arrays.stream(clazz.getDeclaredFields())
            .filter(f -> !f.isSynthetic())
            .filter(f -> !Modifier.isTransient(f.getModifiers()))
            .collect(Collectors.toList());

        Class<?>[] types = fields.stream().map(Field::getType).toArray(Class[]::new);

        Constructor<T> ctor;
        try {
            ctor = clazz.getConstructor(types);
        } catch(NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        if(!Arrays.equals(types, Arrays.stream(ctor.getParameters()).map(Parameter::getType).toArray())) {
            throw new IllegalArgumentException(
                "Malformed object, fields and constructor arguments need to match");
        }

        return r -> {
            Object[] args = fields.stream()
                .map(UncheckedLambda.function(
                    f -> JDBCAdapter.<Object>get(r, transformName(f.getName()), f.getType()),
                    e -> {
                        throw new RuntimeException(e);
                    }
                ))
                .map(o -> o.get())
                .toArray();

            try {
                return ctor.newInstance(args);
            } catch(InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        };
    }

    // TODO Move that to its own class, maybe replace it with a reverse DBMapping
    /* package */ <T> List<Tuple<String, Object>> getValues(T obj) {
        return Arrays.stream(obj.getClass().getDeclaredFields())
            .filter(f -> !f.isSynthetic())
            .filter(f -> !Modifier.isTransient(f.getModifiers()))
            .peek(f -> f.setAccessible(true))
            .map(f -> {
                try {
                    return new Tuple<>(transformName(f.getName()), f.get(obj));
                } catch(IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            })
            .collect(Collectors.toList());
    }

    public <T> Table<T> table(Class<T> clazz) {
        Table<?> table = tables.get(clazz);
        if(table == null) {
            throw new IllegalArgumentException("Unknown mapping for " + clazz.getName());
        }

        return (Table<T>) table;
    }

    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch(SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /* package */ CompletableFuture<ResultSet> readOp(DBOperationSupplier supplier) {
        if(!isConnected()) {
            throw new IllegalStateException("Not connected");
        }

        return readWorker.second.add(() -> supplier.supply().executeQuery());
    }

    /* package */ CompletableFuture<Integer> writeOp(DBOperationSupplier supplier) {
        if(!isConnected()) {
            throw new IllegalStateException("Not connected");
        }

        return writeWorker.second.add(() -> supplier.supply().executeUpdate());
    }

    @Override
    public void close() throws Exception {
        if(!isConnected()) {
            return;
        }

        readWorker.first.interrupt();
        writeWorker.first.interrupt();

        readWorker.first.join();
        writeWorker.first.join();

        connection.close();
    }

    // TODO Move out from here, maybe add a value inside @PrimaryKey instead?
    /* package */ static String transformName(String name) {
        StringBuilder builder = new StringBuilder(name);
        for(int i = 0; i < builder.length(); ++i) {
            if(Character.isUpperCase(builder.charAt(i))) {
                builder.setCharAt(i, Character.toLowerCase(builder.charAt(i)));
                builder.insert(i, '_');
            }
        }
        return builder.toString();
    }
}
