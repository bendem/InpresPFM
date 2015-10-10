package be.hepl.benbear.commons.db;

import java.sql.Date;
import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// TODO This could be rewritten to be more modular
// store it in a Map<Class<?>, Tuple<ToPreparedStatement, FromResultSet>>?
/* package */ final class JDBCAdapter {

    private JDBCAdapter() {}

    private static final Map<Class<?>, JDBCType> JAVA_TO_DB_TYPES;
    static {
        Map<Class<?>, JDBCType> types = new HashMap<>();
        types.put(Integer.class, JDBCType.INTEGER);
        types.put(int.class, JDBCType.INTEGER);
        types.put(Long.class, JDBCType.BIGINT);
        types.put(long.class, JDBCType.BIGINT);
        types.put(Float.class, JDBCType.FLOAT);
        types.put(float.class, JDBCType.FLOAT);
        types.put(Double.class, JDBCType.DOUBLE);
        types.put(double.class, JDBCType.DOUBLE);
        types.put(Boolean.class, JDBCType.BOOLEAN);
        types.put(boolean.class, JDBCType.BOOLEAN);
        types.put(String.class, JDBCType.VARCHAR);
        types.put(Date.class, JDBCType.DATE);
        JAVA_TO_DB_TYPES = Collections.unmodifiableMap(types);
    }

    public static Optional<?> get(ResultSet r, String name, Class<?> clazz) throws SQLException {
        JDBCType type = JAVA_TO_DB_TYPES.get(clazz);

        if(type == null) {
            throw new IllegalArgumentException("Unhandled type: " + clazz.getName());
        }

        Object result;
        switch(type) {
            case INTEGER:
                result = r.getInt(name);
                break;
            case BIGINT:
                result = r.getLong(name);
                break;
            case FLOAT:
                result = r.getFloat(name);
                break;
            case DOUBLE:
                result = r.getDouble(name);
                break;
            case BOOLEAN:
                result = r.getBoolean(name);
                break;
            case VARCHAR:
                result = r.getString(name);
                break;
            case DATE:
                result = r.getDate(name);
                break;
            default:
                throw new SQLFeatureNotSupportedException(type.name() + " not supported");
        }

        if(r.wasNull()) {
            return Optional.empty();
        } else {
            return Optional.of(result);
        }
    }

    public static <T> void set(PreparedStatement stmt, int i, T obj) throws SQLException {
        set(stmt, i, obj, obj.getClass());
    }

    public static <T> void set(PreparedStatement stmt, int i, T obj, Class<? extends T> clazz) throws SQLException {
        JDBCType type = JAVA_TO_DB_TYPES.get(clazz);
        if(type == null) {
            throw new SQLFeatureNotSupportedException(obj.getClass().getName() + " not supported");
        }

        if(obj == null) {
            stmt.setNull(i, type.getVendorTypeNumber());
            return;
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
            case DATE:
                stmt.setDate(i, (Date) obj);
                break;
            default:
                throw new SQLFeatureNotSupportedException(type.name() + " not supported");
        }
    }

}
