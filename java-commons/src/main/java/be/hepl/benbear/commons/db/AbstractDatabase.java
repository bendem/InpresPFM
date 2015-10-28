package be.hepl.benbear.commons.db;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public abstract class AbstractDatabase implements Database {

    protected final Map<Class<?>, Table<?>> tables;

    public AbstractDatabase() {
        this.tables = new ConcurrentHashMap<>();
    }

    @Override
    public <T> Database registerClass(Class<T> clazz) {
        tables.compute(clazz, (k, v) -> createTable(clazz));
        return this;
    }

    protected abstract <T> Table<T> createTable(Class<T> clazz);

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

}
