package be.hepl.benbear.boatapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ContainerMoveSQLiteHelper extends SQLiteOpenHelper {
    public static final String TABLE_CONTAINERS = "containermove";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_CONT_ID = "container_id";
    public static final String COLUMN_CONT_DEST = "container_destination";
    public static final String COLUMN_DATE = "movement_date";
    public static final String COLUMN_ACTION = "movement_action";

    public static final String DB_NAME = "containermove.db";
    private static final int DB_VERSION = 1;

    enum MoveType{
        IN,
        OUT
    }

    public ContainerMoveSQLiteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_CONTAINERS + "(" + COLUMN_ID + " integer primary key autoincrement, "
                        + COLUMN_CONT_ID + " text not null, "
                        + COLUMN_CONT_DEST + " text not null, "
                        + COLUMN_DATE + " date, "
                        + COLUMN_ACTION + " text not null);"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_CONTAINERS + ";");
        onCreate(db);
    }
}
