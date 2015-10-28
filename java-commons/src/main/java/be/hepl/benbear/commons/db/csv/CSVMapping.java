package be.hepl.benbear.commons.db.csv;

import be.hepl.benbear.commons.reflection.FieldReflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CSVMapping {

    public static <T> Function<List<String>, T> toJava(FieldReflection<T> reflection) {
        List<Field> fields = reflection.getFields().collect(Collectors.toList());
        Class<?>[] types = reflection.getTypes().toArray(Class[]::new);

        // Get a constructor matching these types
        Constructor<T> ctor;
        try {
            ctor = reflection.getOwningClass().getConstructor(types);
        } catch(NoSuchMethodException e) {
            throw new RuntimeException(
                "No constructor available to fill all the fields from " + reflection.getOwningClass().getName()
                + ". Prototype needs to match " + Arrays.toString(types), e);
        }

        return columns -> {
            if(columns.size() != fields.size()) {
                throw new IllegalArgumentException("Expected " + fields.size() + " fields, got " + columns.size());
            }

            Object[] args = new Object[columns.size()];
            for(int i = 0; i < columns.size(); ++i) {
                args[i] = toJava(columns.get(i), types[i]);
            }

            try {
                return ctor.newInstance(args);
            } catch(InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static <T> Function<T, String> toCSV(FieldReflection<T> reflection) {
        return obj -> reflection.getValues(obj)
            .map(Object::toString)
            .map(s -> s.replace("\\", "\\\\"))
            .map(s -> s.replace(";", "\\;"))
            .collect(Collectors.joining(";"));
    }

    private static Object toJava(String str, Class<?> clazz) {
        if(clazz == String.class) {
            return str;
        } else if(clazz == int.class) {
            return Integer.parseInt(str);
        } else if(clazz == long.class) {
            return Long.parseLong(str);
        } else if(clazz == double.class) {
            return Double.parseDouble(str);
        } else if(clazz == float.class) {
            return Float.parseFloat(str);
        } else if(clazz == boolean.class) {
            return Boolean.parseBoolean(str);
        } else if(clazz == Instant.class) {
            return Instant.parse(str);
        } else {
            throw new IllegalArgumentException("Unhandled type " + clazz.getName());
        }
    }

    /* package */ static List<String> split(String str) {
        List<String> list = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        char[] chars = str.toCharArray();
        for(int i = 0; i < chars.length; i++) {
            if(chars[i] == '\\') {
                if(i != chars.length - 1) {
                    if(chars[i + 1] == ';') {
                        builder.append(';');
                        ++i;
                        continue;
                    }
                    if(chars[i + 1] == '\\') {
                        builder.append('\\');
                        ++i;
                        continue;
                    }
                }
            } else if(chars[i] == ';') {
                list.add(builder.toString());
                builder.setLength(0);
                continue;
            }
            builder.append(chars[i]);
        }

        list.add(builder.toString());
        return list;
    }

}
