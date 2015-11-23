package be.hepl.benbear.commons.db;

import java.util.Set;

public interface Database extends AutoCloseable {

    enum Driver {

        ORACLE("oracle.jdbc.driver.OracleDriver"),
        ;

        public final String fqdn;

        Driver(String fqdn) {
            this.fqdn = fqdn;
        }

        public void load() {
            try {
                Class.forName(fqdn);
            } catch(ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

    }

    Database connect(String jdbc, String username, String password);

    Database registerClass(Class<?>... classes);

    Set<String> getRegisteredTables();

    <T> Table<T> table(Class<T> clazz);

    boolean isConnected();

}
