package be.hepl.benbear.commons.db;

import java.util.Set;

public interface Database extends AutoCloseable {

    SQLDatabase connect(String jdbc, String username, String password);

    <T> SQLDatabase registerClass(Class<T> clazz);

    Set<String> getRegisteredTables();

    <T> Table<T> table(Class<T> clazz);

    boolean isConnected();

}
