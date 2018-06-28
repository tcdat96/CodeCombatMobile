package tcd.android.com.codecombatmobile.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceScreen;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import tcd.android.com.codecombatmobile.R;
import tcd.android.com.codecombatmobile.util.DataUtil;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        enableBackArrow();
        initUiComponents();
    }

    private void initUiComponents() {
        Button signOutButton = findViewById(R.id.btn_sign_out);
        signOutButton.setOnClickListener(this);
    }

    private void enableBackArrow() {
        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sign_out:
                DataUtil.removeUserData(this);
                triggerRebirth(this);
                break;
        }
    }

    public static void triggerRebirth(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
        if (intent != null) {
            ComponentName componentName = intent.getComponent();
            Intent mainIntent = Intent.makeRestartActivityTask(componentName);
            context.startActivity(mainIntent);
            Runtime.getRuntime().exit(0);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.preferences);

            SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
            PreferenceScreen prefScreen = getPreferenceScreen();

            setAllPreferencesSummary(prefScreen, sharedPreferences);
        }

        private void setAllPreferencesSummary(PreferenceGroup prefGroup, SharedPreferences sharedPreferences) {
            int count = prefGroup.getPreferenceCount();
            for (int i = 0; i < count; i++) {
                Preference pref = prefGroup.getPreference(i);
                if (pref instanceof PreferenceGroup) {
                    setAllPreferencesSummary((PreferenceGroup) pref, sharedPreferences);
                } else if (isSummaryNeeded(pref)) {
                    String value = sharedPreferences.getString(pref.getKey(), "");
                    setPreferenceSummary(pref, value);
                }
            }
        }

        private boolean isSummaryNeeded(Preference pref) {
            return pref instanceof ListPreference || pref instanceof EditTextPreference;
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Preference pref = findPreference(key);
            if (pref != null) {
                if (isSummaryNeeded(pref)) {
                    String value = sharedPreferences.getString(pref.getKey(), "");
                    setPreferenceSummary(pref, value);
                }
            }
        }

        private void setPreferenceSummary(Preference preference, String value) {
            if (preference instanceof EditTextPreference) {
                preference.setSummary(value);
            } else if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(value);
                if (prefIndex >= 0) {
                    listPreference.setSummary(listPreference.getEntries()[prefIndex]);
                }
            }
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }
    }
}
