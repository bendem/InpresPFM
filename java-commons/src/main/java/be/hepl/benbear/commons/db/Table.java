package be.hepl.benbear.commons.db;

import be.hepl.benbear.commons.generics.Tuple;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

// TODO Join, sort, findOne
public interface Table<T> {

    /**
     * Gets the name of the table this object handles.
     *
     * @return the name of the sql table associated with this instance
     */
    String getName();

    /**
     * Retrieves a single row by id. The number of ids provided need to match
     * the value returned by getIdCount.
     *
     * Multiple ids can be provided so that you can use this method with tables
     * with composite primary keys.
     *
     * @param handler a sql error handler
     * @param ids the id to match
     * @return a future that'll contain a mapped object if the id existed or
     *         nothing otherwise
     */
    CompletableFuture<Optional<T>> byId(ErrorHandler handler, Object... ids);

    /**
     * Inserts an object in the database.
     *
     * @param obj the object to insert
     * @return a future that'll contain the number of affected lines (0 or 1 in
     *         this case)
     */
    CompletableFuture<Integer> insert(T obj);

    /**
     * Retrieves a Stream lazily populated with all the rows from the table
     * associated with this object.
     *
     * @return a future that'll be populated with a lazy Stream
     */
    CompletableFuture<Stream<T>> find();

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
     */
    List<Tuple<String, Class<?>>> getIdFields();

}
