package be.hepl.benbear.commons.db;

import java.util.List;

public interface DBPredicate {

    String DEFAULT_COMPARISON = "=";

    static DBPredicate empty() {
        return EmptyDBPredicate.INSTANCE;
    }

    static DBPredicate of(String field, Object value) {
        return new DBPredicateImpl(field, value, DEFAULT_COMPARISON, null, null);
    }

    default DBPredicate and(String field, Object value) {
        return and(field, value, DEFAULT_COMPARISON);
    }

    DBPredicate and(String field, Object value, String comparison);

    default DBPredicate or(String field, Object value) {
        return or(field, value, DEFAULT_COMPARISON);
    }

    DBPredicate or(String field, Object value, String comparison);

    String toSql();

    List<Object> values();

}
