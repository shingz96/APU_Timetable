package com.shing.aputimetable.model;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;

import com.shing.aputimetable.entity.ApuClass;
import com.shing.aputimetable.utils.QueryUtils;

import java.util.List;

/**
 * Created by Shing on 31-Jul-17.
 */

public class ApuClassLoader extends AsyncTaskLoader<List<ApuClass>> {

    private String mIntakeCode;

    public ApuClassLoader(Context context, String intakeCode) {
        super(context);
        mIntakeCode = intakeCode;
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

        // Perform the network request, parse the response, and extract a list of apu class details.
        return QueryUtils.getAllClass(mIntakeCode);
    }
}
