package be.hepl.benbear.commons.db;

import java.util.Collections;
import java.util.List;

/* package */ class EmptyDBPredicate implements DBPredicate {

    /* package */ static final DBPredicate INSTANCE = new EmptyDBPredicate();

    private EmptyDBPredicate() {}

    @Override
    public DBPredicate and(String field, Object value, String comparison) {
        throw new UnsupportedOperationException("Can't and in an empty predicate");
    }

    @Override
    public DBPredicate or(String field, Object value, String comparison) {
        throw new UnsupportedOperationException("Can't or in an empty predicate");
    }

    @Override
    public String toSql() {
        return "";
    }

    @Override
    public List<String> fields() {
        return Collections.emptyList();
    }

    @Override
    public List<Object> values() {
        return Collections.emptyList();
    }
}
