package be.hepl.benbear.commons.db.csv;

import be.hepl.benbear.commons.db.AbstractTable;
import be.hepl.benbear.commons.db.As;
import be.hepl.benbear.commons.db.DBPredicate;
import be.hepl.benbear.commons.reflection.FieldReflection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CSVTable<T> extends AbstractTable<T> {

    private final Path file;
    private final Path fileTmp;
    private final int[] idIndexes;
    private final List<String> fieldNames;
    private final Function<List<String>, T> toJava;
    private final Function<T, String> toCSV;

    public CSVTable(Class<T> clazz, CSVDatabase db) {
        super(clazz);
        file = db.folder.resolve(name + ".csv");
        fileTmp = db.folder.resolve(name + ".tmp");

        List<Field> fields = fieldReflection.getFields().collect(Collectors.toList());
        idIndexes = new int[primaryKeys.size()];
        for(int i = 0, current = 0; i < fields.size(); ++i) {
            if(primaryKeys.containsValue(fields.get(i))) {
                idIndexes[current++] = i;
            }
        }
        System.out.println(clazz.getName() + ": " + Arrays.toString(idIndexes)); // @Debug

        fieldNames = fieldReflection.getFields()
            .map(f -> {
                As as;
                return (as = f.getAnnotation(As.class)) == null ? f.getName() : as.value();
            })
            .collect(Collectors.toList());
        toJava = CSVMapping.toJava(fieldReflection);
        toCSV = CSVMapping.toCSV(fieldReflection);
    }

    @Override
    public CompletableFuture<Optional<T>> byId(Object... ids) {
        if(ids.length == 0 || getIdCount() == 0 || ids.length != getIdCount()) {
            throw new IllegalArgumentException("Table has " + getIdCount() + ", got " + ids.length);
        }

        CompletableFuture<Optional<T>> future = new CompletableFuture<>();

        if(Files.notExists(file)) {
            future.complete(Optional.empty());
            return future;
        }

        Stream<String> lines;
        try {
            lines = Files.lines(file);
        } catch(IOException e) {
            future.completeExceptionally(e);
            return future;
        }

        future.complete(lines
            .map(CSVMapping::split)
            .filter(parts -> {
                for(int i = 0; i < parts.size(); ++i) {
                    if(!ids[i].toString().equals(parts.get(idIndexes[i]))) {
                        return false;
                    }
                }
                return true;
            })
            .map(toJava)
            .findFirst());

        return future;
    }

    @Override
    public CompletableFuture<Integer> insert(T obj) {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        try(BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(toCSV.apply(obj));
            writer.newLine();
        } catch(IOException e) {
            future.completeExceptionally(e);
            return future;
        }

        future.complete(1);
        return future;
    }

    @Override
    public CompletableFuture<Integer> insert(Collection<T> collection) {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        int count = 0;
        try(BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            for(T obj : collection) {
                writer.write(toCSV.apply(obj));
                writer.newLine();
                ++count;
            }
        } catch(IOException e) {
            future.completeExceptionally(e);
            return future;
        }

        future.complete(count);
        return future;
    }

    @Override
    public CompletableFuture<Integer> update(T obj) {
        CompletableFuture<Integer> future = new CompletableFuture<>();

        if(Files.notExists(file)) {
            future.complete(0);
            return future;
        }

        DBPredicate predicate = null;
        for(Map.Entry<String, Field> entry : primaryKeys.entrySet()) {
            Object value = FieldReflection.extractFunction(obj).apply(entry.getValue());
            if(predicate == null) {
                predicate = DBPredicate.of(entry.getKey(), value);
            } else {
                predicate = predicate.and(entry.getKey(), value);
            }
        }

        if(predicate == null) {
            future.completeExceptionally(new RuntimeException("Couldn't build a predicate to update " + name));
            return future;
        }

        int count = 0;
        try(BufferedWriter w = Files.newBufferedWriter(file.resolveSibling(name + ".tmp"));
            BufferedReader r = Files.newBufferedReader(file)) {
            String line;
            while((line = r.readLine()) != null) {
                if(matches(line, predicate)) {
                    ++count;
                    w.write(toCSV.apply(obj));
                } else {
                    w.write(line);
                }
                w.newLine();
            }
        } catch(IOException e) {
            future.completeExceptionally(e);
            return future;
        }

        try {
            Files.move(fileTmp, file, StandardCopyOption.REPLACE_EXISTING);
        } catch(IOException e) {
            future.completeExceptionally(e);
            return future;
        }

        future.complete(count);
        return future;
    }

    @Override
    public CompletableFuture<Integer> update(String field, Object value, DBPredicate predicate) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public CompletableFuture<Integer> delete(DBPredicate predicate) {
        CompletableFuture<Integer> future = new CompletableFuture<>();

        if(Files.notExists(file)) {
            future.complete(0);
            return future;
        }

        int count = 0;
        try(BufferedWriter w = Files.newBufferedWriter(file.resolveSibling(name + ".tmp"));
            BufferedReader r = Files.newBufferedReader(file)) {
            String line;
            while((line = r.readLine()) != null) {
                if(matches(line, predicate)) {
                    ++count;
                } else {
                    w.write(line);
                    w.newLine();
                }
            }
        } catch(IOException e) {
            future.completeExceptionally(e);
            return future;
        }

        try {
            Files.move(fileTmp, file, StandardCopyOption.REPLACE_EXISTING);
        } catch(IOException e) {
            future.completeExceptionally(e);
            return future;
        }

        future.complete(count);
        return future;
    }

    @Override
    public CompletableFuture<Stream<T>> find(DBPredicate predicate) {
        CompletableFuture<Stream<T>> future = new CompletableFuture<>();

        if(Files.notExists(file)) {
            future.complete(Stream.empty());
            return future;
        }

        Stream.Builder<String> builder = Stream.builder();
        try(BufferedReader r = Files.newBufferedReader(file)) {
            String line;
            while((line = r.readLine()) != null) {
                if(matches(line, predicate)) {
                    builder.accept(line);
                }
            }
        } catch(IOException e) {
            future.completeExceptionally(e);
            return future;
        }

        future.complete(builder.build().map(CSVMapping::split).map(toJava));
        return future;
    }

    @Override
    public CompletableFuture<Optional<T>> findOne(DBPredicate predicate) {
        CompletableFuture<Optional<T>> future = new CompletableFuture<>();

        if(Files.notExists(file)) {
            future.complete(Optional.empty());
            return future;
        }

        try(BufferedReader r = Files.newBufferedReader(file)) {
            String line;
            while((line = r.readLine()) != null) {
                if(matches(line, predicate)) {
                    future.complete(Optional.of(toJava.apply(CSVMapping.split(line))));
                    return future;
                }
            }
        } catch(IOException e) {
            future.completeExceptionally(e);
            return future;
        }

        future.complete(Optional.empty());
        return future;
    }

    private boolean matches(String line, DBPredicate predicate) {
        if(predicate.field() == null) {
            // Empty predicate
            return true;
        }

        List<String> parts = CSVMapping.split(line);
        boolean result;
        String field;
        Object value;
        Optional<DBPredicate> next;

        field = predicate.field();
        value = predicate.value();
        result = parts.get(toFieldIndex(field)).equals(value.toString());
        next = predicate.next();

        while(next.isPresent()) {
            predicate = next.get();
            next = predicate.next();
            field = predicate.field();
            value = predicate.value();
            if(predicate.type() == DBPredicate.Type.AND) {
                result = result && parts.get(toFieldIndex(field)).equals(value.toString());
            } else {
                result = result || parts.get(toFieldIndex(field)).equals(value.toString());
            }
        }

        return result;
    }

    private int toFieldIndex(String name) {
        for(int i = 0; i < fieldNames.size(); ++i) {
            if(fieldNames.get(i).equals(name)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Invalid field name: " + name);
    }

}
