package com.shing.aputimetable.model;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;

import com.shing.aputimetable.entity.ApuClass;
import com.shing.aputimetable.model.ApuClassContract.ApuClassEntry;
import com.shing.aputimetable.utils.QueryUtils;

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

        if (TextUtils.isEmpty(mIntakeCode)) {
            return null;
        }

        String[] projection = {
                ApuClassEntry._ID,
                ApuClassEntry.COLUMN_CLASS_DAY,
                ApuClassEntry.COLUMN_CLASS_DATE,
                ApuClassEntry.COLUMN_CLASS_TIME,
                ApuClassEntry.COLUMN_CLASS_ROOM,
                ApuClassEntry.COLUMN_CLASS_LOCATION,
                ApuClassEntry.COLUMN_CLASS_SUBJECT,
                ApuClassEntry.COLUMN_CLASS_LECTURER
        };

        Cursor cursor = mContext.getContentResolver().query(ApuClassEntry.CONTENT_URI, projection, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            List<ApuClass> classes = new ArrayList<>();
            try {
                while (!cursor.isAfterLast()) {
                    ApuClass c = new ApuClass();
                    c.setDate(cursor.getString(cursor.getColumnIndex(ApuClassEntry.COLUMN_CLASS_DATE)));
                    c.setDay(cursor.getString(cursor.getColumnIndex(ApuClassEntry.COLUMN_CLASS_DAY)));
                    c.setTime(cursor.getString(cursor.getColumnIndex(ApuClassEntry.COLUMN_CLASS_TIME)));
                    c.setRoom(cursor.getString(cursor.getColumnIndex(ApuClassEntry.COLUMN_CLASS_ROOM)));
                    c.setLocation(cursor.getString(cursor.getColumnIndex(ApuClassEntry.COLUMN_CLASS_LOCATION)));
                    c.setSubject(cursor.getString(cursor.getColumnIndex(ApuClassEntry.COLUMN_CLASS_SUBJECT)));
                    c.setLecturer(cursor.getString(cursor.getColumnIndex(ApuClassEntry.COLUMN_CLASS_LECTURER)));
                    classes.add(c);
                    cursor.moveToNext();
                }
                return classes;
            } finally {
                cursor.close();
            }
        }

        if (QueryUtils.isNetworkConnected(mContext)) {
            // Perform the network request, parse the response, and extract a list of apu class details.
            return QueryUtils.getAllClass(mIntakeCode, mContext);
        }


        return null;

    }
}
