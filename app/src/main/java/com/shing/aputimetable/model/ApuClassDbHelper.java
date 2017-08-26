package com.shing.aputimetable.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.shing.aputimetable.entity.ApuClass;
import com.shing.aputimetable.model.ApuClassContract.ApuClassEntry;

import java.util.ArrayList;
import java.util.List;

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
    private static final int DATABASE_VERSION = 2;

    /**
     * Constructs a new instance of {@link ApuClassDbHelper}.
     *
     * @param context of the app
     */
    public ApuClassDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static List<ApuClass> getAllClasses(Context context) {
        String[] projection = {
                ApuClassContract.ApuClassEntry._ID,
                ApuClassContract.ApuClassEntry.COLUMN_CLASS_DAY,
                ApuClassContract.ApuClassEntry.COLUMN_CLASS_DATE,
                ApuClassContract.ApuClassEntry.COLUMN_CLASS_TIME,
                ApuClassContract.ApuClassEntry.COLUMN_CLASS_ROOM,
                ApuClassContract.ApuClassEntry.COLUMN_CLASS_LOCATION,
                ApuClassContract.ApuClassEntry.COLUMN_CLASS_SUBJECT,
                ApuClassContract.ApuClassEntry.COLUMN_CLASS_LECTURER,
                ApuClassContract.ApuClassEntry.COLUMN_CLASS_TYPE
        };

        Cursor cursor = context.getContentResolver().query(ApuClassContract.ApuClassEntry.CONTENT_URI, projection, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            List<ApuClass> classes = new ArrayList<>();
            try {
                while (!cursor.isAfterLast()) {
                    ApuClass c = new ApuClass();
                    c.setDate(cursor.getString(cursor.getColumnIndex(ApuClassContract.ApuClassEntry.COLUMN_CLASS_DATE)));
                    c.setDay(cursor.getString(cursor.getColumnIndex(ApuClassContract.ApuClassEntry.COLUMN_CLASS_DAY)));
                    c.setTime(cursor.getString(cursor.getColumnIndex(ApuClassContract.ApuClassEntry.COLUMN_CLASS_TIME)));
                    c.setRoom(cursor.getString(cursor.getColumnIndex(ApuClassContract.ApuClassEntry.COLUMN_CLASS_ROOM)));
                    c.setLocation(cursor.getString(cursor.getColumnIndex(ApuClassContract.ApuClassEntry.COLUMN_CLASS_LOCATION)));
                    c.setSubject(cursor.getString(cursor.getColumnIndex(ApuClassContract.ApuClassEntry.COLUMN_CLASS_SUBJECT)));
                    c.setLecturer(cursor.getString(cursor.getColumnIndex(ApuClassContract.ApuClassEntry.COLUMN_CLASS_LECTURER)));
                    c.setType(cursor.getInt(cursor.getColumnIndex(ApuClassContract.ApuClassEntry.COLUMN_CLASS_TYPE)));
                    classes.add(c);
                    cursor.moveToNext();
                }
                return classes;
            } finally {
                cursor.close();
            }
        }
        return null;
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
                + ApuClassEntry.COLUMN_CLASS_SUBJECT + " TEXT NOT NULL, "
                + ApuClassEntry.COLUMN_CLASS_TYPE + " INTEGER NOT NULL);";

        // Execute the SQL statement
        sqLiteDatabase.execSQL(SQL_CREATE_CLASS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        String SQL_UPDATE_TABLE_V2 = "ALTER TABLE " + ApuClassEntry.TABLE_NAME
                + " ADD COLUMN " + ApuClassEntry.COLUMN_CLASS_TYPE + " INTEGER";

        switch (oldVersion) {
            case 1:
                sqLiteDatabase.execSQL(SQL_UPDATE_TABLE_V2);
        }
    }
}
