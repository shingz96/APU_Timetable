package com.shing.aputimetable.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shing.aputimetable.DataChangeEvent;
import com.shing.aputimetable.R;
import com.shing.aputimetable.adapters.ClassDetailsAdapter;
import com.shing.aputimetable.entity.ApuClass;
import com.shing.aputimetable.model.ApuClassLoader;
import com.shing.aputimetable.model.Database;
import com.shing.aputimetable.utils.MyDateUtils;
import com.shing.aputimetable.utils.QueryUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TodayclassFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<ApuClass>>, SwipeRefreshLayout.OnRefreshListener {


    private static final int APU_CLASS_LOADER_ID = 1;
    private final String TAG = TodayclassFragment.class.getSimpleName();
    private final String INTAKE_CODE_KEY = "intake_code";

    private RecyclerView mRecyclerview;
    private TextView mEmptyTextView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ClassDetailsAdapter classDetailsAdapter;
    private SharedPreferences prefs;

    public TodayclassFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timetable_tab, container, false);
        mRecyclerview = view.findViewById(R.id.recycler_view_card_container);
        mEmptyTextView = view.findViewById(R.id.txt_view_empty);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout_class);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        classDetailsAdapter = new ClassDetailsAdapter(new ArrayList<ApuClass>());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerview.setLayoutManager(layoutManager);
        mRecyclerview.setAdapter(classDetailsAdapter);
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String intake = prefs.getString(INTAKE_CODE_KEY, null);
        if (intake != null) {
            mSwipeRefreshLayout.setEnabled(true);
        } else {
            mSwipeRefreshLayout.setEnabled(false);
        }
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(APU_CLASS_LOADER_ID, null, this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setTitleText();
    }


    @Override
    public Loader<List<ApuClass>> onCreateLoader(int id, Bundle args) {
        String intake = prefs.getString(INTAKE_CODE_KEY, null);
        return new ApuClassLoader(getContext(), intake);
    }

    @Override
    public void onLoadFinished(Loader<List<ApuClass>> loader, List<ApuClass> data) {
        Database.clearAll();
        Database db = Database.getDatabaseInstance();
        db.initData(data);
        classDetailsAdapter.setDataset(null);
        classDetailsAdapter.notifyDataSetChanged();
        List<ApuClass> todayClass = db.getDataByDay(MyDateUtils.getTodayIndex());

        if (prefs.getString("filter", "0").equals("1")) {
            todayClass = db.filterByClass(todayClass, prefs);
        } else {
            todayClass = db.filterByType(todayClass, prefs);
        }

        if (todayClass == null || todayClass.isEmpty()) {
            mRecyclerview.setVisibility(View.GONE);
            mEmptyTextView.setVisibility(View.VISIBLE);
            mEmptyTextView.setText(R.string.no_class_text);
        } else {
            mEmptyTextView.setVisibility(View.GONE);
            mRecyclerview.setVisibility(View.VISIBLE);
            classDetailsAdapter.setDataset(todayClass);
            classDetailsAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onLoaderReset(Loader<List<ApuClass>> loader) {
        classDetailsAdapter.setDataset(null);
        classDetailsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        QueryUtils.requestNewTimetable(getActivity(), prefs);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onMessageEvent(DataChangeEvent event) {
        classDetailsAdapter.setDataset(null);
        classDetailsAdapter.notifyDataSetChanged();
        setTitleText();
        getLoaderManager().restartLoader(APU_CLASS_LOADER_ID, null, this);
    }

    private void setTitleText() {
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            date = MyDateUtils.getMondayDate();
        }
        StringBuilder titleStringBuilder = new StringBuilder();
        titleStringBuilder.append(prefs.getString(INTAKE_CODE_KEY, ""));
        if (!titleStringBuilder.toString().isEmpty()) {
            titleStringBuilder.append(" - ");
        }
        titleStringBuilder.append(MyDateUtils.formatDate(date, "EEE, MMM dd"));
        getActivity().setTitle(titleStringBuilder.toString());
    }
}
