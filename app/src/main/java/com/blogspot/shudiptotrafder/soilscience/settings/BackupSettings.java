package com.blogspot.shudiptotrafder.soilscience.settings;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.widget.Toast;

import com.blogspot.shudiptotrafder.soilscience.R;
import com.blogspot.shudiptotrafder.soilscience.utilities.ConstantUtils;
import com.blogspot.shudiptotrafder.soilscience.utilities.FileImportExportUtills;

/**
 * Created by Shudipto on 7/10/2017.
 */

public class BackupSettings extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

//    private static final String TAG = AdvanceSettingsFragment.class.getName();
//    public static final String PAGE_ID = "page_id";

    private static final int PERMISSIONS_REQUEST_READ_STORAGE_FAVOURITE = 0;
    private static final int PERMISSIONS_REQUEST_READ_STORAGE_ADDED = 1;
    private static final int PERMISSIONS_REQUEST_WRITE_STORAGE_FAVOURITE = 2;
    private static final int PERMISSIONS_REQUEST_WRITE_STORAGE_ADDED = 3;


//    public static BackupSettings newInstance(String pageId) {
//        BackupSettings f = new BackupSettings();
//        Bundle args = new Bundle();
//        args.putString(PAGE_ID, pageId);
//        f.setArguments(args);
//        return (f);
//    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        Preference preference = findPreference(key);


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
        addPreferencesFromResource(R.xml.pref_backup);

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

        //my all preference
        Preference exportFavourite = findPreference(getString(R.string.bps_ex_fav_key));
        Preference importFavourite = findPreference(getString(R.string.bps_im_fav_key));
        Preference exportAddWord = findPreference(getString(R.string.bps_ex_add_key));
        Preference importAddWord = findPreference(getString(R.string.bps_im_add_key));


        //export favourite
        exportFavourite.setOnPreferenceClickListener(preference -> {

            if (Build.VERSION.SDK_INT >= 23) {

                if (getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
                            , PERMISSIONS_REQUEST_WRITE_STORAGE_FAVOURITE);
                } else {
                    writeFavouriteData();
                }

            } else {
                writeFavouriteData();
            }

            return false;
        });

        //import favourite
        importFavourite.setOnPreferenceClickListener(preference -> {

            if (Build.VERSION.SDK_INT >= 23) {

                if (getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                            , PERMISSIONS_REQUEST_READ_STORAGE_FAVOURITE);
                } else {
                    readFavouriteData();
                }

            } else {
                readFavouriteData();
            }

            return false;
        });

        //export added word
        exportAddWord.setOnPreferenceClickListener(preference -> {

            if (Build.VERSION.SDK_INT >= 23) {

                if (getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
                            , PERMISSIONS_REQUEST_WRITE_STORAGE_ADDED);
                } else {
                    writeAddedWord();
                }

            } else {
                writeAddedWord();
            }

            return false;
        });

        //import added word
        importAddWord.setOnPreferenceClickListener(preference -> {

            if (Build.VERSION.SDK_INT >= 23) {

                if (getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                            , PERMISSIONS_REQUEST_READ_STORAGE_ADDED);
                } else {
                    readAddedWord();
                }

            } else {
                readAddedWord();
            }

            return false;
        });

    }


    //read write favourite data
    private void readFavouriteData() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {

            FileImportExportUtills.importFile(getContext(), ConstantUtils.SETTING_IMOUT_OPTION_FAVOUTITR);

        } else {
            Toast.makeText(getContext(), "Something went wrong.Your storage is not readable." +
                            " Your storage option is different from others.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void writeFavouriteData() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            FileImportExportUtills.exportFileFavourite(getContext());

        } else {
            Toast.makeText(getContext(), "Something went wrong.Your storage is not writable." +
                            " Your storage option is different from others.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    //readWrite add word
    private void writeAddedWord(){
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            FileImportExportUtills.exportFileUser(getContext());
        } else {
            Toast.makeText(getContext(), "Something went wrong.Your storage is not writable." +
                            " Your storage option is different from others.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void readAddedWord(){
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            FileImportExportUtills.importFile(getContext(),ConstantUtils.SETTING_IMOUT_OPTION_USER);
        } else {
            Toast.makeText(getContext(), "Something went wrong.Your storage is not readable." +
                            " Your storage option is different from others.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        switch (requestCode) {

            case PERMISSIONS_REQUEST_READ_STORAGE_FAVOURITE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission added
                    readFavouriteData();
                } else {
                    Toast.makeText(getContext(), "Sorry You don't Backup" +
                            " your data.To backup your data you need to grant permission", Toast.LENGTH_SHORT).show();
                }
                break;

            case PERMISSIONS_REQUEST_READ_STORAGE_ADDED:

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    readAddedWord();

                } else {
                    Toast.makeText(getContext(), "Sorry You don't Backup" +
                            " your data.To backup your data you need to grant permission", Toast.LENGTH_SHORT).show();
                }
                break;

            case PERMISSIONS_REQUEST_WRITE_STORAGE_FAVOURITE:

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    writeFavouriteData();

                } else {
                    Toast.makeText(getContext(), "Sorry You don't Backup" +
                            " your data.To backup your data you need to grant permission", Toast.LENGTH_SHORT).show();
                }
                break;

            case PERMISSIONS_REQUEST_WRITE_STORAGE_ADDED:

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    writeAddedWord();

                } else {
                    Toast.makeText(getContext(), "Sorry You don't Backup" +
                            " your data.To backup your data you need to grant permission", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }
    }


}
