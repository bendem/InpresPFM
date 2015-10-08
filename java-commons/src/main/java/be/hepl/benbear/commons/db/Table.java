package be.hepl.benbear.commons.db;

import be.hepl.benbear.commons.generics.Tuple;

import java.lang.reflect.Modifier;
import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Table<T> {

    protected static final Map<Class<?>, JDBCType> JAVA_TO_DB_TYPES;
    static {
        Map<Class<?>, JDBCType> types = new HashMap<>();
        types.put(Integer.class, JDBCType.INTEGER);
        types.put(Long.class, JDBCType.BIGINT);
        types.put(Float.class, JDBCType.FLOAT);
        types.put(Double.class, JDBCType.DOUBLE);
        types.put(Boolean.class, JDBCType.BOOLEAN);
        types.put(String.class, JDBCType.VARCHAR);
        JAVA_TO_DB_TYPES = Collections.unmodifiableMap(types);
    }

    protected final String name;
    protected final DBMappingFunction.DBMapping<T> mapper;
    protected Database db;

    protected Table(String name, DBMappingFunction.DBMapping<T> mapper) {
        this.name = name;
        this.mapper = mapper;
    }

    /* package */ Table setDb(Database db) {
        this.db = db;
        return this;
    }

    public String getName() {
        return name;
    }

    public CompletableFuture<Integer> insert(T obj) {
        List<Tuple<String, Object>> v = getValues(obj);
        String columns = v.stream().map(Tuple::getFirst).collect(Collectors.joining(", "));
        String values = Stream.generate(() -> "?").limit(v.size()).collect(Collectors.joining(", "));

        return db.writeOp(() -> {
            PreparedStatement stmt = db.connection.prepareStatement(
                "insert into " + name + "(" + columns + ") values (" + values + ")");
            for(int i = 0; i < v.size(); ++i) {
                set(i + 1, stmt, v.get(i).second);
            }
            return stmt;
        });
    }

    private List<Tuple<String, Object>> getValues(T obj) {
        return Arrays.stream(obj.getClass().getDeclaredFields())
            .peek(f -> System.out.println(f.getName()))
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

    private static String transformName(String name) {
        StringBuilder builder = new StringBuilder(name);
        for(int i = 0; i < builder.length(); ++i) {
            if(Character.isUpperCase(builder.charAt(i))) {
                builder.setCharAt(i, Character.toLowerCase(builder.charAt(i)));
                builder.insert(i, '_');
            }
        }
        return builder.toString();
    }

    public abstract int getIdCount();
    public abstract List<String> getIdFields();

    protected static <T> void set(int i, PreparedStatement stmt, T obj) throws SQLException {
        if(obj == null) {
            throw new IllegalArgumentException("Null not supported");
        }

        JDBCType type = JAVA_TO_DB_TYPES.get(obj.getClass());
        if(type == null) {
            throw new SQLFeatureNotSupportedException(obj.getClass().getName() + " not supported");
        }

        switch(type) {
            case INTEGER:
                stmt.setInt(i, (Integer) obj);
                break;
            case BIGINT:
                stmt.setLong(i, (Long) obj);
                break;
            case FLOAT:
                stmt.setFloat(i, (Float) obj);
                break;
            case DOUBLE:
                stmt.setDouble(i, (Double) obj);
                break;
            case BOOLEAN:
                stmt.setBoolean(i, (Boolean) obj);
                break;
            case VARCHAR:
                stmt.setString(i, (String) obj);
                break;
            default:
                throw new SQLFeatureNotSupportedException(type.name() + " not supported");
        }
    }

}
