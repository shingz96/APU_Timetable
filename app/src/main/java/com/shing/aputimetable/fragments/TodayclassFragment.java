package com.shing.aputimetable.fragments;


import android.os.Bundle;
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
import android.widget.Toast;

import com.shing.aputimetable.R;
import com.shing.aputimetable.adapters.ClassDetailsAdapter;
import com.shing.aputimetable.entity.ApuClass;
import com.shing.aputimetable.model.ApuClassContract;
import com.shing.aputimetable.model.ApuClassLoader;
import com.shing.aputimetable.model.Database;
import com.shing.aputimetable.utils.MyDateUtils;
import com.shing.aputimetable.utils.QueryUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TodayclassFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<ApuClass>>, SwipeRefreshLayout.OnRefreshListener {


    private static final int APU_CLASS_LOADER_ID = 1;
    private final String TAG = TodayclassFragment.class.getSimpleName();
    private RecyclerView mRecyclerview;
    private TextView mEmptyTextView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ClassDetailsAdapter classDetailsAdapter;

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

        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(APU_CLASS_LOADER_ID, null, this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle("Today Class - " + MyDateUtils.formatDate(new Date(), "MMM, dd"));
    }


    @Override
    public Loader<List<ApuClass>> onCreateLoader(int id, Bundle args) {
        return new ApuClassLoader(getContext(), "uc3f1702se");
    }

    @Override
    public void onLoadFinished(Loader<List<ApuClass>> loader, List<ApuClass> data) {
        Database.clearAll();
        Database db = Database.getDatabaseInstance();
        db.initData(data);

        List todayClass = db.getDataByDay(MyDateUtils.getTodayIndex());
        if (todayClass == null || todayClass.isEmpty()) {
            mRecyclerview.setVisibility(View.GONE);
            mEmptyTextView.setVisibility(View.VISIBLE);
            mEmptyTextView.setText("No Class Found");
        } else {
            mRecyclerview.setVisibility(View.VISIBLE);
            classDetailsAdapter.setDataset(todayClass);
            classDetailsAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onLoaderReset(Loader<List<ApuClass>> loader) {
    }

    @Override
    public void onRefresh() {
        String toastText;
        if (QueryUtils.isNetworkConnected(getContext())) {
            //delete previous data
            getContext().getContentResolver().delete(ApuClassContract.ApuClassEntry.CONTENT_URI, null, null);
            getLoaderManager().restartLoader(APU_CLASS_LOADER_ID, null, this);
            toastText = "Refreshed";
        } else {
            toastText = "No Network!";
        }
        Toast.makeText(getContext(), toastText, Toast.LENGTH_SHORT).show();
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
