package be.hepl.benbear.commons.db;

import java.sql.SQLException;

@FunctionalInterface
public interface DBOperation<T> {

    T call() throws SQLException;

}
