package be.hepl.benbear.commons.db;

import be.hepl.benbear.commons.streams.UncheckedLambda;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/* package */ class Mapping {

    @FunctionalInterface
    /* package */ interface DBToJavaMapping<T> {

        /**
         * Represents a method that can convert a ResultSet to a
         * java object.
         *
         * The implementation must not call any ResultSet method to select
         * another result from the ResultSet.
         */
        T apply(ResultSet r) throws SQLException;
    }

    @FunctionalInterface
    /* package */ interface JavaToDBMapping<T> {

        /**
         * Represents a method that can fill a PreparedStatement with the values
         * of a java object.
         *
         * The implementation must set the query string of the provided
         * PreparedStatement as well as its argument, but it must not execute
         * it.
         */
        void accept(T obj, PreparedStatement s) throws SQLException;
    }

    /**
     * Returns a method to convert a ResultSet to a java object.
     *
     * @param clazz the class of the object to construct
     * @param <T> the type of the object
     * @return the constructed object
     */
    /* package */ static <T> DBToJavaMapping<T> createDBToJavaMapping(Class<T> clazz) {
        // Collect the class fields
        List<Field> fields = Arrays.stream(clazz.getDeclaredFields())
            // Maybe we should only collect the fields with a specific annotation?
            .filter(f -> !f.isSynthetic())
            .filter(f -> !Modifier.isTransient(f.getModifiers()))
            .collect(Collectors.toList());

        //Collect the types of the collected fields
        Class<?>[] types = fields.stream().map(Field::getType).toArray(Class[]::new);

        // Get a constructor matching these types
        Constructor<T> ctor;
        try {
            ctor = clazz.getConstructor(types);
        } catch(NoSuchMethodException e) {
            throw new RuntimeException(
                "No constructor available to fill all the fields from " + clazz.getName()
                + ". Prototype needs to match " + Arrays.toString(types), e);
        }

        return r -> {
            // Retrieve all the values the ResultSet based on the class fields
            Object[] args = fields.stream()
                .map(UncheckedLambda.function(f -> {
                        As as = f.getAnnotation(As.class);
                        String name;
                        if(as == null) {
                            name = transformName(f.getName());
                        } else {
                            name = as.value();
                        }

                        return JDBCAdapter.get(r, name, f.getType());
                    },
                    e -> {
                        throw new RuntimeException(e);
                    }
                ))
                .map(o -> o.orElse(null)) // Non present values are null
                .toArray();

            try {
                return ctor.newInstance(args);
            } catch(InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        };
    }

    /**
     * Transforms camel case java names to underscored sql names by inserting an
     * underscore before each uppercase letter.
     *
     * @param name the java camel cased name
     * @return an underscored name matching sql style
     */
    /* package */ static String transformName(String name) {
        StringBuilder builder = new StringBuilder(name);
        for(int i = 0; i < builder.length(); ++i) {
            if(Character.isUpperCase(builder.charAt(i))) {
                builder.setCharAt(i, Character.toLowerCase(builder.charAt(i)));
                builder.insert(i, '_');
            }
        }
        return builder.toString();
    }

}
