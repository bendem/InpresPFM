package be.hepl.benbear.commons.db;

import java.sql.SQLException;
import java.util.function.Consumer;

@FunctionalInterface
public interface ErrorHandler extends Consumer<SQLException> {
}
