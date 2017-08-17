package com.shing.aputimetable;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.shing.aputimetable.model.ApuClassContract;
import com.shing.aputimetable.utils.QueryUtils;

import org.greenrobot.eventbus.EventBus;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        boolean useDarkTheme = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_theme", false);

        if (useDarkTheme) {
            setTheme(R.style.AppTheme_Dark);
        }
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new SettingsFragment()).commit();
        }

    }

    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        private SharedPreferences prefs;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

            EditTextPreference p = (EditTextPreference) findPreference("intake_code");
            String intake = prefs.getString("intake_code", "");
            if (intake.equals("")) {
                p.setSummary("Please provide your Intake Code");
            } else {
                p.setSummary(intake);
            }

//            findPreference("intake_code").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//                public boolean onPreferenceClick(Preference preference) {
//                    Toast.makeText(getActivity(),"clicked",Toast.LENGTH_SHORT).show();
//                    return true;
//                }
//            });

            findPreference("intake_code").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    Boolean save = true;
                    if (false) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Invalid Input");
                        builder.setMessage("Something's gone wrong...");
                        builder.setPositiveButton(android.R.string.ok, null);
                        builder.show();
                        save = false;
                    }
                    return save;
                }
            });

        }

        @Override
        public void onResume() {
            super.onResume();
            prefs.registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            prefs.unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            switch (key) {
                case "dark_theme":
                    getActivity().recreate();
                    EventBus.getDefault().post(new ReloadEvent());
                    break;
                case "intake_code":
                    EditTextPreference p = (EditTextPreference) findPreference(key);
                    p.setText(p.getText().toUpperCase());
                    p.setSummary(p.getText());
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                getActivity().getContentResolver().delete(ApuClassContract.ApuClassEntry.CONTENT_URI, null, null);
                                QueryUtils.getAllClass(prefs.getString("intake_code", ""), getActivity());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    t.start();
                    try {
                        t.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    EventBus.getDefault().post(new DataChangeEvent());
                    break;
            }
        }

    }

}
