package be.hepl.benbear.commons.db;

import be.hepl.benbear.commons.generics.Tuple;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Stream;

public interface Table<T> {

    String getName();

    CompletableFuture<Optional<T>> byId(Consumer<SQLException> handler, Object... ids);

    CompletableFuture<Integer> insert(T obj);

    CompletableFuture<Stream<T>> find();

    CompletableFuture<Stream<T>> find(DBPredicate predicate);

    int getIdCount();

    List<Tuple<String, Class<?>>> getIdFields();

}
