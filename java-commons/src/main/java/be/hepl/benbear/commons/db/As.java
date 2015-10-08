package be.hepl.benbear.commons.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines the name of a field explicitly instead of relying on automatic detection.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface As {

    /**
     * The name of the corresponding column in the sql table.
     */
    String value();

}
