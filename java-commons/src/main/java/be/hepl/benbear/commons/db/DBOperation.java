package be.hepl.benbear.commons.db;

import java.sql.SQLException;

/**
 * Represents an operation on a database susceptible to throw a SQLException.
 *
 * @param <T> the return type of the operation
 */
@FunctionalInterface
public interface DBOperation<T> {

    T call() throws SQLException;

}
