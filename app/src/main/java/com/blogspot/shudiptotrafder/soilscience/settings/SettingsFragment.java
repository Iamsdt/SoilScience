package com.blogspot.shudiptotrafder.soilscience.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.blogspot.shudiptotrafder.soilscience.R;

/**
 * Created by Shudipto on 6/6/2017.
 */


public class SettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

<<<<<<< Updated upstream
=======
//    private static final String TAG = SettingsFragment.class.getName();
//    public static final String PAGE_ID = "page_id";
//
//    public SettingsFragment newInstance(String pageId) {
//        SettingsFragment f = new SettingsFragment();
//        Bundle args = new Bundle();
//        args.putString(PAGE_ID, pageId);
//        f.setArguments(args);
//        return f;
//    }

>>>>>>> Stashed changes
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        Preference preference = findPreference(key);


        //if for re queries

        if (preference != null) {

            if (!(preference instanceof CheckBoxPreference)) {
                String value = sharedPreferences.getString(preference.getKey(), "");
                setPreferenceSummery(preference, value);
            }
        }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        // Add 'general' preferences, defined in the XML file
        addPreferencesFromResource(R.xml.pref_general);

        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();

        PreferenceScreen preferenceScreen = getPreferenceScreen();

        int count = preferenceScreen.getPreferenceCount();

        for (int i = 0; i < count; i++) {
            Preference p = preferenceScreen.getPreference(i);
            if (!(p instanceof CheckBoxPreference)) {
                String value = sharedPreferences.getString(p.getKey(), "");
                setPreferenceSummery(p, value);
            }
        }

    }

    private void setPreferenceSummery(Preference preference, Object value) {

        String stringValue = value.toString();

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            //same code in one line
            //int prefIndex = ((ListPreference) preference).findIndexOfValue(value);

            //prefIndex must be is equal or garter than zero because
            //array count as 0 to ....
            if (prefIndex >= 0) {
                listPreference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            // For other preferences, set the summary to the value's simple string representation.
            preference.setSummary(stringValue);
        }
    }

    //register and unregister on lifecycle
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

}

