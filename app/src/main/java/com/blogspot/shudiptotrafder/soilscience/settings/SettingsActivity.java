package com.blogspot.shudiptotrafder.soilscience.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
<<<<<<< Updated upstream
import android.support.v7.preference.PreferenceManager;
=======
>>>>>>> Stashed changes
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.blogspot.shudiptotrafder.soilscience.R;
import com.blogspot.shudiptotrafder.soilscience.theme.ThemeUtils;

<<<<<<< Updated upstream
=======
//public class SettingsActivity extends AppCompatActivity implements
//        PreferenceFragmentCompat.OnPreferenceStartScreenCallback {

>>>>>>> Stashed changes
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ThemeUtils.initialize(this);

        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);

        //is switch preference changed then recreate activity
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener((sharedPreferences, key) -> {
            recreate();
            //Log.e("setting","recreated");
        });

<<<<<<< Updated upstream
=======
//        FragmentManager fragmentManager = getSupportFragmentManager();
//
//        Fragment fragment;
//
//        if (savedInstanceState == null) {
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragment = new SettingsFragment().newInstance("Advanced_Setting");
//            fragmentTransaction.add(R.id.settingsContainer, fragment);
//            fragmentTransaction.commit();
//        }
>>>>>>> Stashed changes

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //buy calling android.R.id.home

        int id = item.getItemId();

        if (id == android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

<<<<<<< Updated upstream
=======
//    @Override
//    public boolean onPreferenceStartScreen(PreferenceFragmentCompat caller, PreferenceScreen preferenceScreen) {
//        //Log.d(TAG, "callback called to attach the preference sub screen");
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        AdvanceSettingsFragment fragment = AdvanceSettingsFragment.newInstance("Advanced Settings Subscreen");
//        BackupSettings backupSettings = BackupSettings.newInstance("BackUp Settings");
//        Bundle args = new Bundle();
//        //Defining the sub screen as new root for the  subscreen
//        args.putString(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT, preferenceScreen.getKey());
//        fragment.setArguments(args);
//
//        if (preferenceScreen.getKey().equalsIgnoreCase("backup")) {
//
//            ft.replace(R.id.settingsContainer, backupSettings, preferenceScreen.getKey());
//
//        } else {
//            ft.replace(R.id.settingsContainer, fragment, preferenceScreen.getKey());
//        }
//
//        ft.addToBackStack(null);
//        ft.commit();
//        return true;
//    }
>>>>>>> Stashed changes
}
