package be.hepl.benbear.commons.logging;

public class Log {

    public enum Type {
        DEBUG, INFO, WARNING, ERROR
    }

    public static void d(String str) {
        log(Type.DEBUG, str, null);
    }

    public static void i(String str) {
        log(Type.INFO, str, null);
    }

    public static void w(String str) {
        log(Type.WARNING, str, null);
    }

    public static void w(String str, Throwable throwable) {
        log(Type.WARNING, str, throwable);
    }

    public static void e(String str) {
        log(Type.ERROR, str, null);
    }

    public static void e(String str, Throwable throwable) {
        log(Type.ERROR, str, throwable);
    }

    public static synchronized void log(Type type, String str, Throwable throwable) {
        // TODO That can be much better, but you know, time is missing
        System.err.printf("[%s] %s%n", type, str);
        if(throwable != null) {
            System.err.println(throwable.getMessage());
            throwable.printStackTrace();
        }
    }

}
