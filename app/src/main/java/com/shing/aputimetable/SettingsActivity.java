package com.shing.aputimetable;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.shing.aputimetable.entity.ApuClass;
import com.shing.aputimetable.model.ApuClassDbHelper;
import com.shing.aputimetable.utils.QueryUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        String DARK_THEME_KEY = "dark_theme";
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
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.settings_title);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new SettingsFragment()).commit();
        }
    }


    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        private final String INTAKE_CODE_KEY = "intake_code";
        private final String DARK_THEME_KEY = "dark_theme";
        private final String FILTER_KEY = "filter";
        private final String TYPE_FILTER_KEY = "type_filter_list";
        private final String CLASS_FILTER_PREFS = "class_filter_prefs";
        private final String CLASS_FILTER_KEY = "class_filter_list";
        private final String CLASS_FILTER_VALUE_KEY = "class_filter_list_value";
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

            Preference classPreference = findPreference(CLASS_FILTER_PREFS);
            classPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    displayClassFilterDialog(addClassList());
                    return true;
                }
            });

            ListPreference l = (ListPreference) findPreference(FILTER_KEY);
            if (l.getValue().equals("0")) {
                findPreference(TYPE_FILTER_KEY).setEnabled(true);
                findPreference(CLASS_FILTER_PREFS).setEnabled(false);
            } else {
                findPreference(TYPE_FILTER_KEY).setEnabled(false);
                findPreference(CLASS_FILTER_PREFS).setEnabled(true);
            }


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
            ListPreference l = (ListPreference) findPreference(FILTER_KEY);
            switch (key) {
                case DARK_THEME_KEY:
                    getActivity().recreate();
                    EventBus.getDefault().post(new ReloadEvent());
                    break;
                case INTAKE_CODE_KEY:
                    EditTextPreference p = (EditTextPreference) findPreference(key);
                    p.setText(p.getText().toUpperCase());
                    p.setSummary(p.getText());
                    QueryUtils.requestNewTimetable(getActivity(), prefs);
                    prefs.edit()
                            .remove(CLASS_FILTER_KEY)
                            .remove(CLASS_FILTER_VALUE_KEY)
                            .apply();
                    l.setValue("0");
                    break;
                case FILTER_KEY:
                    if (l.getValue().equals("0")) {
                        findPreference(TYPE_FILTER_KEY).setEnabled(true);
                        findPreference(CLASS_FILTER_PREFS).setEnabled(false);
                    } else {
                        findPreference(TYPE_FILTER_KEY).setEnabled(false);
                        findPreference(CLASS_FILTER_PREFS).setEnabled(true);
                    }
                    break;
            }
        }

        private String[] sortClassBySubject(String[] input) {
            int n = input.length;
            String temp;
            for (int i = 0; i < n; i++) {
                for (int j = 1; j < (n - i); j++) {
                    char c1 = 'Z'; 
                    char c2 = 'Z';
                    String s1 = "";
                    String s2 = "";
                    if (input[j - 1].split("-").length >= 4) {
                        c1 = input[j - 1].split("-")[3].trim().charAt(0);
                        s1 = input[j - 1].split("-")[3].trim();
                    }
                    if (input[j].split("-").length >= 4) {
                        c2 = input[j].split("-")[3].trim().charAt(0);
                        s2 = input[j].split("-")[3].trim();
                    }
                    if (c1 > c2) {
                        //swap elements
                        temp = input[j - 1];
                        input[j - 1] = input[j];
                        input[j] = temp;
                    } else if (c1 == c2 && s1.equals(s2) && (input[j - 1].length() > input[j].length())) {
                                temp = input[j - 1];
                                input[j - 1] = input[j];
                                input[j] = temp;
                    }
                }
            }
            return input;
        }

        private String[] addClassList() {
            List<ApuClass> classes = ApuClassDbHelper.getAllClasses(getActivity());
            List<String> tmp = new ArrayList<>();
            if (classes != null) {
                for (int i = 0; i < classes.size(); i++) {
                    tmp.add(classes.get(i).getSubject());
                }

                //remove possible duplicate
                Set<String> hs = new HashSet<>();
                hs.addAll(tmp);
                tmp.clear();
                tmp.addAll(hs);

                String[] arr = new String[tmp.size()];
                arr = tmp.toArray(arr);
                arr = sortClassBySubject(arr);
                return arr;
            }
            return null;
        }

        private void displayClassFilterDialog(CharSequence[] args) {
            if (args != null) {
                //get selected from share preference
                Set<String> precheck = prefs.getStringSet(CLASS_FILTER_VALUE_KEY, null);
                Integer[] selected;
                if (precheck != null) {
                    String[] tmp = precheck.toArray(new String[precheck.size()]);
                    selected = new Integer[precheck.size()];
                    for (int i = 0; i < tmp.length; i++) {
                        selected[i] = Integer.parseInt(tmp[i]);
                    }
                } else {
                    selected = new Integer[args.length];
                    for (int i = 0; i < selected.length; i++) {
                        selected[i] = i;
                    }
                }

                new MaterialDialog.Builder(getActivity())
                        .title(R.string.class_filter_title)
                        .items(args)
                        .itemsCallbackMultiChoice(selected, new MaterialDialog.ListCallbackMultiChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                return true;
                            }
                        })
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Integer[] input = dialog.getSelectedIndices();
                                List items = dialog.getItems();
                                Set<String> entrySet = null;
                                Set<String> entryValueSet = null;
                                if (input != null && items != null) {
                                    List<Integer> indeces = Arrays.asList(input);
                                    ArrayList<String> entries = new ArrayList<>();
                                    ArrayList<String> values = new ArrayList<>();
                                    for (int i = 0; i < indeces.size(); i++) {
                                        entries.add(items.get(indeces.get(i)).toString());
                                        values.add(String.valueOf(indeces.get(i)));
                                    }
                                    entrySet = new HashSet<>(entries);
                                    entryValueSet = new HashSet<>(values);
                                }
                                prefs.edit().putStringSet(CLASS_FILTER_KEY, entrySet).apply();
                                prefs.edit().putStringSet(CLASS_FILTER_VALUE_KEY, entryValueSet).apply();
                                dialog.dismiss();
                            }
                        })
                        .onNeutral(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.clearSelectedIndices();
                            }
                        })
                        .neutralText("Clear")
                        .positiveText(android.R.string.ok)
                        .autoDismiss(false)
                        .show();
            } else {
                new MaterialDialog.Builder(getActivity())
                        .title(R.string.class_filter_title)
                        .content("Nothing to filter!")
                        .positiveText(android.R.string.ok)
                        .show();
            }
        }

    }

}
