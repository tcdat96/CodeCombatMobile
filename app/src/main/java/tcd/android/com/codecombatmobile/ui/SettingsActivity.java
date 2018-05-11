package tcd.android.com.codecombatmobile.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v14.preference.SwitchPreference;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceScreen;
import android.view.MenuItem;

import tcd.android.com.codecombatmobile.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

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
