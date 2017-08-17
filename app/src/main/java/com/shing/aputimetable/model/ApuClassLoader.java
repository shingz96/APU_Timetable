package com.shing.aputimetable.model;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;
import android.util.Log;

import com.shing.aputimetable.entity.ApuClass;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shing on 31-Jul-17.
 */

public class ApuClassLoader extends AsyncTaskLoader<List<ApuClass>> {

    private String mIntakeCode;
    private Context mContext;

    public ApuClassLoader(Context context, String intakeCode) {
        super(context);
        mIntakeCode = intakeCode;
        mContext = context;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }


    @Override
    public List<ApuClass> loadInBackground() {
        Log.d("Loader Class", "Load in background");
        if (TextUtils.isEmpty(mIntakeCode)) {
            return null;
        }

        List l = getFromSQLite();
        if (l != null) {
            return l;
        }

        return null;

    }

    public List<ApuClass> getFromSQLite() {
        String[] projection = {
                ApuClassContract.ApuClassEntry._ID,
                ApuClassContract.ApuClassEntry.COLUMN_CLASS_DAY,
                ApuClassContract.ApuClassEntry.COLUMN_CLASS_DATE,
                ApuClassContract.ApuClassEntry.COLUMN_CLASS_TIME,
                ApuClassContract.ApuClassEntry.COLUMN_CLASS_ROOM,
                ApuClassContract.ApuClassEntry.COLUMN_CLASS_LOCATION,
                ApuClassContract.ApuClassEntry.COLUMN_CLASS_SUBJECT,
                ApuClassContract.ApuClassEntry.COLUMN_CLASS_LECTURER
        };

        Cursor cursor = mContext.getContentResolver().query(ApuClassContract.ApuClassEntry.CONTENT_URI, projection, null, null, null);

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
}
