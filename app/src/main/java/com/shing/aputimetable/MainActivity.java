package com.shing.aputimetable;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.shing.aputimetable.fragments.TimetableFragment;
import com.shing.aputimetable.fragments.TodayclassFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = MainActivity.class.getSimpleName();

    private final Handler mDrawerHandler = new Handler();
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private int selectedDrawerItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.toolbar);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);

        setSupportActionBar(mToolbar);
        setupNavigationDrawer();

        if (savedInstanceState == null) {
            selectedDrawerItem = R.id.item_todayclass_fragment;
            navigate(selectedDrawerItem);
            mNavigationView.setCheckedItem(selectedDrawerItem);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("drawer", selectedDrawerItem);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        navigate(savedInstanceState.getInt("drawer"));
    }

    private void setupNavigationDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                super.onDrawerSlide(drawerView, 0); // this disables the arrow @ completed tate
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, 0); // this disables the animation
            }
        };


        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);

    }

    private void navigate(int id) {
        selectedDrawerItem = id;
        mNavigationView.setCheckedItem(id);

        switch (id) {
            case R.id.item_timetable_fragment:
                if (getSupportFragmentManager().findFragmentByTag("timetable") == null)
                    displayFragment(new TimetableFragment(), "timetable");
                break;
            case R.id.item_todayclass_fragment:
                if (getSupportFragmentManager().findFragmentByTag("todayclass") == null)
                    displayFragment(new TodayclassFragment(), "todayclass");
                break;
        }

    }

    private void displayFragment(@NonNull Fragment navFragment, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        supportInvalidateOptionsMenu();
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        transaction.replace(R.id.content_frame, navFragment, tag).commitNow();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        mDrawerHandler.removeCallbacksAndMessages(null);
        mDrawerHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                navigate(item.getItemId());
            }
        }, 250);
        mDrawerLayout.closeDrawer(mNavigationView);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(mNavigationView)) {
            mDrawerLayout.closeDrawer(mNavigationView);
        } else {
            finish();
        }
    }
}
