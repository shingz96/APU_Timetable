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

    private final String DARK_THEME_KEY = "dark_theme";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        boolean useDarkTheme = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(DARK_THEME_KEY, false);

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

        private final String INTAKE_CODE_KEY = "intake_code";
        private final String DARK_THEME_KEY = "dark_theme";
        private SharedPreferences prefs;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

            EditTextPreference p = (EditTextPreference) findPreference(INTAKE_CODE_KEY);
            String intake = prefs.getString(INTAKE_CODE_KEY, "");
            if (intake.equals("")) {
                p.setSummary(R.string.intake_code_summary);
            } else {
                p.setSummary(intake);
            }

            findPreference(INTAKE_CODE_KEY).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    Boolean save = true;
                    EditTextPreference p = (EditTextPreference) findPreference(INTAKE_CODE_KEY);
                    if (p.getEditText().getText().toString().trim().isEmpty()) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Invalid Input");
                        builder.setMessage("Empty Intake Code Is Not Allow!");
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
                case DARK_THEME_KEY:
                    getActivity().recreate();
                    EventBus.getDefault().post(new ReloadEvent());
                    break;
                case INTAKE_CODE_KEY:
                    EditTextPreference p = (EditTextPreference) findPreference(key);
                    p.setText(p.getText().toUpperCase());
                    p.setSummary(p.getText());
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                getActivity().getContentResolver().delete(ApuClassContract.ApuClassEntry.CONTENT_URI, null, null);
                                QueryUtils.getAllClass(prefs.getString(INTAKE_CODE_KEY, ""), getActivity());
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
