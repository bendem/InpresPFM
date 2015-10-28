package be.hepl.benbear.boatapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;

public class ContainerMoveDAO {
    private SQLiteDatabase database;
    private ContainerMoveSQLiteHelper dbHelper;

    private String[] listColumns = {
            ContainerMoveSQLiteHelper.COLUMN_ID,
            ContainerMoveSQLiteHelper.COLUMN_CONT_ID,
            ContainerMoveSQLiteHelper.COLUMN_CONT_DEST,
            ContainerMoveSQLiteHelper.COLUMN_DATE,
            ContainerMoveSQLiteHelper.COLUMN_ACTION};

    public ContainerMoveDAO(Context context) {
        dbHelper = new ContainerMoveSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void addContainerMove(String containerId, String containerDest, long date, String action) {
        ContentValues values = new ContentValues();
        values.put(ContainerMoveSQLiteHelper.COLUMN_CONT_ID, containerId);
        values.put(ContainerMoveSQLiteHelper.COLUMN_CONT_DEST, containerDest);
        values.put(ContainerMoveSQLiteHelper.COLUMN_DATE, date);
        values.put(ContainerMoveSQLiteHelper.COLUMN_ACTION, action);

        database.insert(ContainerMoveSQLiteHelper.TABLE_CONTAINERS, null, values);
    }

    public Cursor getCursorContainerMove() {
        return database.query(ContainerMoveSQLiteHelper.TABLE_CONTAINERS, listColumns, null, null, null, null, null);
    }
}
