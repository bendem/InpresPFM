package be.hepl.benbear.commons.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface DBOperationSupplier {

    PreparedStatement supply() throws SQLException;

}
