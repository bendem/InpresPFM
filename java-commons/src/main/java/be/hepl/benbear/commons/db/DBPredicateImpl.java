package be.hepl.benbear.commons.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DBPredicateImpl implements DBPredicate {

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
        if(next != null) {
            return next.and(field, value, comparison);
        }
        return next = new DBPredicateImpl(field, value, comparison, Type.AND, first);
    }

    @Override
    public DBPredicate or(String field, Object value, String comparison) {
        if(next != null) {
            return next.or(field, value, comparison);
        }
        return next = new DBPredicateImpl(field, value, comparison, Type.OR, first);
    }

    @Override
    public String toSql() {
        if(first == this) {
            return toSql(new StringBuilder(" where ")).toString();
        }

        return first.toSql();
    }

    @Override
    public String field() {
        return field;
    }

    @Override
    public Object value() {
        return value;
    }

    @Override
    public Type type() {
        return type;
    }

    @Override
    public Optional<DBPredicate> next() {
        return Optional.ofNullable(next);
    }

    private StringBuilder toSql(StringBuilder builder) {
        builder
            .append(field)
            .append(' ')
            .append(comparison)
            .append(' ')
            .append("?");

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
    public List<String> fields() {
        if(first == this) {
            return fields(new ArrayList<>());
        }

        return first.fields(new ArrayList<>());
    }

    private List<String> fields(List<String> list) {
        list.add(field);

        if(next != null) {
            next.fields(list);
        }

        return list;
    }

    @Override
    public List<Object> values() {
        if(first == this) {
            return values(new ArrayList<>());
        }

        return first.values(new ArrayList<>());
    }

    private List<Object> values(List<Object> list) {
        list.add(value);

        if(next != null) {
            next.values(list);
        }

        return list;
    }
}
