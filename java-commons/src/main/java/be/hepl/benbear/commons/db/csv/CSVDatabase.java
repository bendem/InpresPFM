package be.hepl.benbear.commons.db.csv;

import be.hepl.benbear.commons.db.AbstractDatabase;
import be.hepl.benbear.commons.db.Database;
import be.hepl.benbear.commons.db.Table;

import java.nio.file.Path;
import java.nio.file.Paths;

public class CSVDatabase extends AbstractDatabase {

    /* package */ Path folder;

    @Override
    public <T> Database registerClass(Class<T> clazz) {
        if(!isConnected()) {
            throw new IllegalStateException("You need to call connect before registering classes");
        }
        return super.registerClass(clazz);
    }

    /**
     * Sets the folder to find the csv files in.
     *
     * @param folder the path to the folder
     * @param username ignored
     * @param password ignored
     * @return the Database instance
     */
    @Override
    public CSVDatabase connect(String folder, String username, String password) {
        this.folder = Paths.get(folder);
        return this;
    }

    @Override
    public boolean isConnected() {
        return folder != null;
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
