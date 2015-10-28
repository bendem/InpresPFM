package be.hepl.benbear.commons.db;

import be.hepl.benbear.commons.generics.Tuple;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

// TODO Doc
public class SQLDatabase implements Database {

    // I'm against this, a thread pool would make much more sense
    // but they made me do it...
    private final Tuple<Thread, DBRunnable<ResultSet>> readWorker;
    private final Tuple<Thread, DBRunnable<Integer>> writeWorker;
    private final Map<Class<?>, Table<?>> tables;
    /* package */ Connection connection;

    // Someone told me this should be a bean, so there you go, empty constructor
    public SQLDatabase() {
        DBRunnable<ResultSet> read = new DBRunnable<>();
        readWorker = new Tuple<>(new Thread(read), read);

        DBRunnable<Integer> write = new DBRunnable<>();
        writeWorker = new Tuple<>(new Thread(write), write);

        tables = new ConcurrentHashMap<>();
    }

    @Override
    public SQLDatabase connect(String jdbc, String username, String password) {
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

    @Override
    public <T> SQLDatabase registerClass(Class<T> clazz) {
        TableImpl<T> table = new TableImpl<>(clazz, this);
        tables.put(clazz, table);
        return this;
    }

    @Override
    public Set<String> getRegisteredTables() {
        return tables.values().stream().map(Table::getName).collect(Collectors.toSet());
    }

    @Override
    public <T> Table<T> table(Class<T> clazz) {
        Table<?> table = tables.get(clazz);
        if(table == null) {
            throw new IllegalArgumentException("Unknown mapping for " + clazz.getName());
        }

        return (Table<T>) table;
    }

    @Override
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch(SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected CompletableFuture<ResultSet> readOp(DBOperationSupplier supplier) {
        if(!isConnected()) {
            throw new IllegalStateException("Not connected");
        }

        return readWorker.second.add(() -> supplier.supply().executeQuery());
    }

    protected CompletableFuture<Integer> writeOp(DBOperationSupplier supplier) {
        if(!isConnected()) {
            throw new IllegalStateException("Not connected");
        }

        return writeWorker.second.add(() -> {
            try(PreparedStatement stmt = supplier.supply()) {
                return stmt.executeUpdate();
            }
        });
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

}
