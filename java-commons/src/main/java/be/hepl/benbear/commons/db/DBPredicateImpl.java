package be.hepl.benbear.commons.db;

import java.util.ArrayList;
import java.util.List;

public class DBPredicateImpl implements DBPredicate {

    private enum Type {
        AND, OR
    }

    private final String field;
    private final Object value;
    private final String comparison;
    private final Type type;
    private final DBPredicateImpl first;
    private DBPredicateImpl next;

    /* package */ DBPredicateImpl(String field, Object value, String comparison, Type type, DBPredicateImpl first) {
        this.field = field;
        this.value = value;
        this.comparison = comparison;
        this.type = type;
        this.first = first == null ? this : first;
    }

    @Override
    public DBPredicate and(String field, Object value, String comparison) {
        return next = new DBPredicateImpl(field, value, comparison, Type.AND, first);
    }

    @Override
    public DBPredicate or(String field, Object value, String comparison) {
        return next = new DBPredicateImpl(field, value, comparison, Type.OR, first);
    }

    @Override
    public String toSql() {
        if(this.first == this) {
            return toSql(new StringBuilder(" where ")).toString();
        }

        return first.toSql();
    }

    private StringBuilder toSql(StringBuilder builder) {
        builder
            .append(field)
            .append(' ')
            .append(comparison)
            .append(' ')
            .append(value == null ? "null" : "?");

        if(next != null) {
            builder
                .append(' ')
                .append(next.type.name())
                .append(' ');
            next.toSql(builder);
        }

        return builder;
    }

    @Override
    public List<Object> values() {
        if(first == this) {
            return values(new ArrayList<>());
        }

        return first.values();
    }

    private List<Object> values(List<Object> list) {
        if(value != null) {
            list.add(value);
        }

        return list;
    }
}
