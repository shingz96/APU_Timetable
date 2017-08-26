package com.shing.aputimetable;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.shing.aputimetable.fragments.TimetableFragment;
import com.shing.aputimetable.fragments.TodayclassFragment;
import com.shing.aputimetable.model.ApuClassContract;
import com.shing.aputimetable.utils.MyDateUtils;
import com.shing.aputimetable.utils.QueryUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = MainActivity.class.getSimpleName();

    private final Handler mDrawerHandler = new Handler();
    private final Handler checkHandler = new Handler();
    private final String INTAKE_CODE_KEY = "intake_code";
    private final String DARK_THEME_KEY = "dark_theme";
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private int selectedDrawerItem;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean useDarkTheme = prefs.getBoolean(DARK_THEME_KEY, false);

        if (useDarkTheme) {
            setTheme(R.style.AppTheme_Dark);
        }

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

        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.add(Calendar.DATE, 1);
        final long timeInterval = cal.getTimeInMillis() - System.currentTimeMillis();

        Log.d(TAG, "run shecdule task at " + cal.getTime().toString() + " still left " + (timeInterval / 1000) / 60 + " mins");
        checkHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "shecdule task is run");
                EventBus.getDefault().post(new DataChangeEvent());
            }
        }, timeInterval);

        String intake = prefs.getString(INTAKE_CODE_KEY, "");
        long last_update_msec = prefs.getLong("last_update", 0);
        Calendar last_update_date = Calendar.getInstance();
        last_update_date.setTimeInMillis(last_update_msec);
        Calendar today = Calendar.getInstance();
        boolean sameDay = last_update_date.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                last_update_date.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR);

        if (intake.equals("")) {
            showIntakeCodeEditTextDialog();
        } else if (!sameDay) {
            checkNewTimetable();
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
        if (savedInstanceState.getInt("drawer") != R.id.item_settings_activity && savedInstanceState.getInt("drawer") != R.id.item_about_activity)
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
            case R.id.item_settings_activity:
                Intent settings = new Intent(this, SettingsActivity.class);
                startActivity(settings);
                break;
            case R.id.item_about_activity:
                Intent about = new Intent(this, AboutActivity.class);
                startActivity(about);
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

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        checkHandler.removeCallbacksAndMessages(null);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void restartActivity(ReloadEvent event) {
        recreate();
    }

    private void checkNewTimetable() {
        checkHandler.post(new Runnable() {
            @Override
            public void run() {
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

                Cursor cursor = getContentResolver().query(ApuClassContract.ApuClassEntry.CONTENT_URI, projection, ApuClassContract.ApuClassEntry.COLUMN_CLASS_DATE + "=?", new String[]{MyDateUtils.formatDate(MyDateUtils.getMondayDate(), "dd-MMM-YYYY")}, null);
                if (cursor != null && cursor.moveToFirst()) {
                    Log.d(TAG, "ald have");
                    cursor.close();
                } else {
                    Log.d(TAG, "refreshed");
                    QueryUtils.requestNewTimetable(getApplicationContext(), prefs);
                }
                prefs.edit().putLong("last_update", System.currentTimeMillis()).apply();
            }
        });
    }

    private void showIntakeCodeEditTextDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.intake_code_title)
                .content("Can be reset in Settings if needed.")
                .inputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS)
                .alwaysCallInputCallback()
                .positiveText(android.R.string.ok)
                .input(
                        null,
                        null,
                        false,
                        new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                if (input.toString().trim().length() > 7) {
                                    dialog.getContentView().setText("Can be reset in Settings if needed");
                                    dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                                } else {
                                    dialog.getContentView().setText(R.string.invalid_intake_code_warning);
                                    dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                                }

                            }
                        })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        prefs.edit().putString(INTAKE_CODE_KEY, dialog.getInputEditText().getText().toString().trim().toUpperCase()).apply();
                        QueryUtils.requestNewTimetable(getApplicationContext(), prefs);
                        View todayClassFragmentView = getSupportFragmentManager().findFragmentByTag("todayclass").getView();
                        if (todayClassFragmentView != null) {
                            todayClassFragmentView.findViewById(R.id.swipe_refresh_layout_class).setEnabled(true);

                        }
                    }
                })
                .show();
    }
}
