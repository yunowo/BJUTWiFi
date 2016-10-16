package me.liuyun.bjutlgn.ui;

import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import me.liuyun.bjutlgn.BuildConfig;
import me.liuyun.bjutlgn.R;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        findPreference("version").setSummary(BuildConfig.VERSION_NAME);
        findPreference("licenses").setSummary(R.string.licenses);
        findPreference("user").setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getContext(), UserActivity.class);
            getContext().startActivity(intent);
            return true;
        });
    }

    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = (preference, value) -> {
        String stringValue = value.toString();
        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(stringValue);
            preference.setSummary(
                    index >= 0
                            ? listPreference.getEntries()[index]
                            : null);
        } else if (preference.getKey().equals("password")) {
            preference.setSummary("********");
        } else {
            preference.setSummary(stringValue);
        }
        return true;
    };

}