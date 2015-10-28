package be.hepl.benbear.commons.db.csv;

import be.hepl.benbear.commons.db.AbstractDatabase;
import be.hepl.benbear.commons.db.Table;

import java.nio.file.Path;
import java.nio.file.Paths;

public class CSVDatabase extends AbstractDatabase {

    /* package */ Path file;

    @Override
    public CSVDatabase connect(String path, String username, String password) {
        file = Paths.get(path);
        return this;
    }

    @Override
    public boolean isConnected() {
        return file != null;
    }

    @Override
    public void close() throws Exception {
        // NOP
    }

    @Override
    protected <T> Table<T> createTable(Class<T> clazz) {
        return new CSVTable<>(clazz, this);
    }

}
