package com.shing.aputimetable.model;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;
import android.util.Log;

import com.shing.aputimetable.entity.ApuClass;

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

        List l = ApuClassDbHelper.getAllClasses(mContext);
        if (l != null) {
            return l;
        }

        return null;

    }
}
