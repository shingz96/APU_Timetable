package com.shing.aputimetable.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shing.aputimetable.R;
import com.shing.aputimetable.adapters.TimetableTabAdapter;
import com.shing.aputimetable.utils.MyDateUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimetableFragment extends Fragment {

    private final String TAG = TimetableFragment.class.getSimpleName();

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    public TimetableFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timetable, container, false);

        mTabLayout = view.findViewById(R.id.tab);
        mViewPager = view.findViewById(R.id.timetable_container);

        //add fragments to adapter
        TimetableTabAdapter adapter = new TimetableTabAdapter(getChildFragmentManager());
        for (int i = 0; i < 5; i++) {
            TimetableTabFragment tab = new TimetableTabFragment();
            Bundle b = new Bundle();
            b.putInt("day", i);
            tab.setArguments(b);
            adapter.addFragment(tab, MyDateUtils.getDayFromNum(i));
        }

        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle("Timetable - " + MyDateUtils.formatDate(MyDateUtils.getMondayDate(), "MMM, dd"));
    }

}
