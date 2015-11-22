package be.hepl.benbear.commons.logging;

public class Log {

    public enum Type {
        DEBUG, INFO, WARNING, ERROR
    }

    public static void d(String str, Object... objects) {
        log(Type.DEBUG, null, str, objects);
    }

    public static void i(String str, Object... objects) {
        log(Type.INFO, null, str, objects);
    }

    public static void w(String str, Object... objects) {
        log(Type.WARNING, null, str, objects);
    }

    public static void w(String str, Throwable throwable, Object... objects) {
        log(Type.WARNING, throwable, str, objects);
    }

    public static void e(String str, Object... objects) {
        log(Type.ERROR, null, str, objects);
    }

    public static void e(String str, Throwable throwable, Object... objects) {
        log(Type.ERROR, throwable, str, objects);
    }

    public static synchronized void log(Type type, Throwable throwable, String str, Object... objects) {
        // TODO That can be much better, but you know, time is missing
        System.err.printf("[%s] %s%n", type, String.format(str, objects));
        if(throwable != null) {
            System.err.println(throwable.getMessage());
            throwable.printStackTrace();
        }
    }

}
