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

    public static void log(Type type, Throwable throwable, String str, Object... objects) {
        // TODO That can be much better, but you know, don't have time
        System.err.printf("[%s] [%s] [%s] %s%n",
            type, Thread.currentThread().getName(), findClassFromStack(), String.format(str, objects));

        if(throwable != null) {
            System.err.println(throwable.getMessage());
            throwable.printStackTrace();
        }
    }

    private static String findClassFromStack() {
        boolean foundLog = false;
        for(StackTraceElement e : Thread.currentThread().getStackTrace()) {
            if(!foundLog && e.getClassName().equals(Log.class.getName())) {
                foundLog = true;
                continue;
            }
            if(foundLog && !e.getClassName().equals(Log.class.getName())) {
                return fqdnToClassName(e.getClassName()) + '.' + e.getMethodName() + ':' + e.getLineNumber();
            }
        }
        return "n/a";
    }

    private static String fqdnToClassName(String fqdn) {
        int i = fqdn.lastIndexOf('.');
        if(i < 0) {
            return fqdn;
        }
        return fqdn.substring(i + 1);
    }

}
