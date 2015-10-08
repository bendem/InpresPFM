package be.hepl.benbear.commons.db;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
