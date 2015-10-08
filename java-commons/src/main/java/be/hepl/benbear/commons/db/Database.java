package be.hepl.benbear.commons.db;

import be.hepl.benbear.commons.generics.Tuple;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class Database implements AutoCloseable {

    // I'm against this, a thread pool would make much more sense
    // but they made me do it...
    private final Tuple<Thread, DbRunnable<ResultSet>> readWorker;
    private final Tuple<Thread, DbRunnable<Integer>> writeWorker;
    private final Map<String, Table<?>> tables;
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

    public <T> Database registerTable(Class<T> clazz, Table<T> table) {
        table.setDb(this);
        tables.put(table.getName().toLowerCase(), table);
        return this;
    }

    public <T, TableType extends Table<T>> TableType table(String name) {
        TableType table = (TableType) tables.get(name.toLowerCase());
        if(table == null) {
            throw new IllegalArgumentException("Unknown table: " + name);
        }

        return table;
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
            throw new IllegalStateException("There are no open connections to close");
        }

        readWorker.first.interrupt();
        writeWorker.first.interrupt();

        readWorker.first.join();
        writeWorker.first.join();

        connection.close();
    }
}