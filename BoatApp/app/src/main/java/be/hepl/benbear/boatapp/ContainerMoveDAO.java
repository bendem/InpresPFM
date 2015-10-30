package be.hepl.benbear.boatapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ContainerMoveDAO {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("y-M-d");
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

    public void addContainerMove(String containerId, String containerDest, Date date, ContainerMoveSQLiteHelper.MoveType action) {
        ContentValues values = new ContentValues();
        values.put(ContainerMoveSQLiteHelper.COLUMN_CONT_ID, containerId);
        values.put(ContainerMoveSQLiteHelper.COLUMN_CONT_DEST, containerDest);
        values.put(ContainerMoveSQLiteHelper.COLUMN_DATE, DATE_FORMAT.format(date));
        values.put(ContainerMoveSQLiteHelper.COLUMN_ACTION, action.toString());

        database.insert(ContainerMoveSQLiteHelper.TABLE_CONTAINERS, null, values);
    }

    public Map<Date, Long> getCountMoveDay(ContainerMoveSQLiteHelper.MoveType type) {
        Map<Date, Long> result = new HashMap<>();

        Cursor c = database.rawQuery("select date(" + ContainerMoveSQLiteHelper.COLUMN_DATE +"), count(*) from " + ContainerMoveSQLiteHelper.TABLE_CONTAINERS +
                " where " + ContainerMoveSQLiteHelper.COLUMN_ACTION + " = '" + type.toString() +
                "' group by date(" + ContainerMoveSQLiteHelper.COLUMN_DATE +")", null);

        c.moveToFirst();
        while(!c.isAfterLast()) {
            try {
                result.put(DATE_FORMAT.parse(c.getString(0)), c.getLong(1));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            c.moveToNext();
        }
        c.close();

        return result;
    }

    public Map<String,Map<String, Integer>> getCountMovePerDestinationPerWeek(ContainerMoveSQLiteHelper.MoveType type) {
        Map<String,Map<String, Integer>> result = new HashMap<>();

        Cursor c = database.rawQuery("select strftime('%Y-%W', " + ContainerMoveSQLiteHelper.COLUMN_DATE +", 'unixepoch')" +", "+
                ContainerMoveSQLiteHelper.COLUMN_CONT_DEST +", count(*) from " + ContainerMoveSQLiteHelper.TABLE_CONTAINERS +
                " where " + ContainerMoveSQLiteHelper.COLUMN_ACTION + " = '" + type.toString() +
                "' group by "+ ContainerMoveSQLiteHelper.COLUMN_CONT_DEST +", strftime('%Y-%W', " + ContainerMoveSQLiteHelper.COLUMN_DATE +", 'unixepoch')" +
                " order by 1,2;",null);
        c.moveToFirst();

        while(!c.isAfterLast()) {
            String tmp = c.getString(0);
            result.put(tmp, new HashMap<String, Integer>());
            while(!c.isAfterLast() && c.getString(0).equals(tmp)) {
                result.get(tmp).put(c.getString(1), c.getInt(2));
                c.moveToNext();
            }
        }
        c.close();

        return result;
    }
}
