package be.hepl.benbear.commons.db;

import java.util.List;

/**
 * Represents a sql predicate.
 */
public interface DBPredicate {

    String DEFAULT_COMPARISON = "=";

    /**
     * Returns an immutable no-op predicate.
     */
    static DBPredicate empty() {
        return EmptyDBPredicate.INSTANCE;
    }

    /**
     * Returns a predicate that'll represent the equality of a column value and
     * a provided value.
     *
     * @param field the field name of the table
     * @param value the value of the comparison
     * @return the constructed predicate
     */
    static DBPredicate of(String field, Object value) {
        return new DBPredicateImpl(field, value, DEFAULT_COMPARISON, null, null);
    }

    /**
     * Returns a predicate that'll represent the comparison of a column value and
     * a provided value using a custom comparison.
     *
     * @param field the field name of the table
     * @param value the value of the comparison
     * @return the constructed predicate
     */
    static DBPredicate of(String field, Object value, String comparison) {
        return new DBPredicateImpl(field, value, comparison, null, null);
    }

    /**
     * Chains the predicate with another predicate using AND and equality.
     *
     * @param field the field name of the table
     * @param value the value of the comparison
     * @return the chained predicate
     */
    default DBPredicate and(String field, Object value) {
        return and(field, value, DEFAULT_COMPARISON);
    }

    /**
     * Chains the predicate with another predicate using AND and a custom
     * comparison.
     *
     * @param field the field name of the table
     * @param value the value of the comparison
     * @param comparison the comparison to use (i.e. ">", "<", "<=", "<>")
     * @return the chained predicate
     */
    DBPredicate and(String field, Object value, String comparison);

    /**
     * Chains the predicate with another predicate using OR and equality.
     *
     * @param field the field name of the table
     * @param value the value of the comparison
     * @return the chained predicate
     */
    default DBPredicate or(String field, Object value) {
        return or(field, value, DEFAULT_COMPARISON);
    }

    /**
     * Chains the predicate with another predicate using OR and a custom
     * comparison.
     *
     * @param field the field name of the table
     * @param value the value of the comparison
     * @param comparison the comparison to use (i.e. ">", "<", "<=", "<>")
     * @return the chained predicate
     */
    DBPredicate or(String field, Object value, String comparison);

    /**
     * Converts the predicate to a usable sql predicate starting with " where ".
     * @return the sql predicate
     */
    String toSql();

    /**
     * Returns the values to bind to this the generated sql predicate.
     */
    List<Object> values();

}
