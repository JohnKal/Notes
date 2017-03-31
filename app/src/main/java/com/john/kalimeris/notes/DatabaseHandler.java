package com.john.kalimeris.notes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by John on 02/10/16.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 3;

    public static final String TABLE = "notes";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE= "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_DATE= "date";
    public static final String COLUMN_COLOR= "color";
    public static final String COLUMN_EVENT_ID= "eventId";
    public static final String COLUMN_CHECK= "check_note";

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE + "( " + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_TITLE
            + " text not null, " + COLUMN_DESCRIPTION
            + " text, " + COLUMN_DATE
            + " text, " + COLUMN_COLOR
            + " text, " + COLUMN_EVENT_ID
            + " INTEGER DEFAULT -1, " + COLUMN_CHECK
            + " INTEGER DEFAULT 0);";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DatabaseHandler.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }
}
