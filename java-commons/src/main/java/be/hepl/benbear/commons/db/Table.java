package be.hepl.benbear.commons.db;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

// TODO Join, sort
public interface Table<T> {

    /**
     * Gets the name of the table this object handles.
     *
     * @return the name of the sql table associated with this instance
     */
    String getName();

    /**
     * Gets the class of the java mapping.
     */
    Class<T> getTableClass();

    /**
     * Retrieves a single row by id. The number of ids provided need to match
     * the value returned by getIdCount.
     *
     * Multiple ids can be provided so that you can use this method with tables
     * with composite primary keys.
     *
     * @param ids the id to match
     * @return a future that'll contain a mapped object if the id existed or
     *         nothing otherwise
     */
    CompletableFuture<Optional<T>> byId(Object... ids);

    /**
     * Inserts an object in the database.
     *
     * @param obj the object to insert
     * @return a future that'll contain the number of affected lines (0 or 1 in
     *         this case)
     */
    CompletableFuture<Integer> insert(T obj);

    /**
     * Updates an object in the database based on its primary key.
     *
     * @param obj the new object to update (the predicate will be based on the
     *            object id)
     * @return a future that'll container the number of affected lines
     */
    CompletableFuture<Integer> update(T obj);

    CompletableFuture<Integer> deleteById(Object...ids);

    CompletableFuture<Integer> delete(DBPredicate predicate);

    /**
     * Retrieves a Stream lazily populated with all the rows from the table
     * associated with this object.
     *
     * @return a future that'll be populated with a lazy Stream
     */
    default CompletableFuture<Stream<T>> find() {
        return find(DBPredicate.empty());
    }

    /**
     * Retrieves a Stream lazily populated with all the rows from the table
     * associated with this object that match the provided predicate.
     *
     * @param predicate a sql predicate
     * @return a future that'll be populated with a lazy Stream
     */
    CompletableFuture<Stream<T>> find(DBPredicate predicate);

    /**
     * Finds the first row matching the provide predicate.
     *
     * @param predicate the predicate to match
     * @return a future that'll be populated with the first row matching the
     *         provided predicate if any.
     */
    CompletableFuture<Optional<T>> findOne(DBPredicate predicate);

    /**
     * Gets the number of fields composing the primary key.
     */
    int getIdCount();

    /**
     * Gets the field names and types of the fields composing the primary key.
     *
     * The returned map is ordered based on the field order of the underlying
     * object.
     */
    Map<String, Class<?>> getIdFields();

}
