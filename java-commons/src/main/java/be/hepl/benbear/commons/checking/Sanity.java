package be.hepl.benbear.commons.checking;

public class Sanity {

    public static <T> T notNull(T obj, String name) {
        if(obj == null) {
            throw new IllegalArgumentException(name + " cannot be null");
        }
        return obj;
    }

    public static void noneNull(Object...objects) {
        for(Object object : objects) {
            if(object == null) {
                throw new IllegalArgumentException("An argument was null");
            }
        }
    }

    public static void isTrue(boolean cond, String error) {
        if(!cond) {
            throw new IllegalArgumentException(error);
        }
    }

}
