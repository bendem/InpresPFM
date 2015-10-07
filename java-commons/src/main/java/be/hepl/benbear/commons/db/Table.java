package be.hepl.benbear.commons.db;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLType;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Table<T> {

    protected static final Map<Class<?>, SQLType> JAVA_TO_DB_TYPES;
    static {
        Map<Class<?>, SQLType> types = new HashMap<>();
        types.put(int.class, JDBCType.INTEGER);
        types.put(long.class, JDBCType.BIGINT);
        types.put(float.class, JDBCType.FLOAT);
        types.put(double.class, JDBCType.DOUBLE);
        types.put(boolean.class, JDBCType.BOOLEAN);
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

    protected static void set(int i, PreparedStatement stmt, Object obj) throws SQLException {
        stmt.setObject(i, obj, JAVA_TO_DB_TYPES.get(obj.getClass()));
    }

}
