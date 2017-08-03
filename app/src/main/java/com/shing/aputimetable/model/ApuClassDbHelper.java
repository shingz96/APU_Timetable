package com.shing.aputimetable.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.shing.aputimetable.model.ApuClassContract.ApuClassEntry;

/**
 * Created by Shing on 31-Jul-17.
 */

public class ApuClassDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = ApuClassDbHelper.class.getSimpleName();

    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "apu.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link ApuClassDbHelper}.
     *
     * @param context of the app
     */
    public ApuClassDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_CLASS_TABLE = "CREATE TABLE " + ApuClassEntry.TABLE_NAME + " ("
                + ApuClassEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ApuClassEntry.COLUMN_CLASS_DATE + " TEXT NOT NULL, "
                + ApuClassEntry.COLUMN_CLASS_TIME + " TEXT NOT NULL, "
                + ApuClassEntry.COLUMN_CLASS_DAY + " TEXT NOT NULL, "
                + ApuClassEntry.COLUMN_CLASS_ROOM + " TEXT NOT NULL, "
                + ApuClassEntry.COLUMN_CLASS_LOCATION + " TEXT NOT NULL, "
                + ApuClassEntry.COLUMN_CLASS_LECTURER + " TEXT NOT NULL, "
                + ApuClassEntry.COLUMN_CLASS_SUBJECT + " TEXT NOT NULL);";

        // Execute the SQL statement
        sqLiteDatabase.execSQL(SQL_CREATE_CLASS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
