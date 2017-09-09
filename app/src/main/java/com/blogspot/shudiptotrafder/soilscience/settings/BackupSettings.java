/*
 * Copyright {2017} {Shudipto Trafder}
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blogspot.shudiptotrafder.soilscience.settings;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.widget.Toast;

import com.blogspot.shudiptotrafder.soilscience.R;
import com.blogspot.shudiptotrafder.soilscience.utilities.ConstantUtils;
import com.blogspot.shudiptotrafder.soilscience.utilities.FileImportExportUtils;
import com.codekidlabs.storagechooser.StorageChooser;
import com.codekidlabs.storagechooser.utils.DiskUtil;

/**
 * Created by Shudipto Trafder on 7/10/2017.
 * ${PACKAGE_NAME}
 */

public class BackupSettings extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

//    private static final String TAG = AdvanceSettingsFragment.class.getName();
//    public static final String PAGE_ID = "page_id";

    //permission code
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

//        Preference preference = findPreference(key);
//
//
//        if (preference != null) {
//
//            if (!(preference instanceof CheckBoxPreference)) {
//                String value = sharedPreferences.getString(preference.getKey(), "");
//                setPreferenceSummery(preference, value);
//            }
//        }
    }

    private String path = null;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        // Add 'general' preferences, defined in the XML file
        addPreferencesFromResource(R.xml.pref_backup);

        SharedPreferences sp = getContext().getSharedPreferences(ConstantUtils.STORAGE_PATH_KEY, Context.MODE_PRIVATE);
        path = sp.getString(DiskUtil.SC_PREFERENCE_KEY,
                ConstantUtils.DEAFUALT_PATH_STORAGE);

        //my all preference
        Preference exportFavourite = findPreference(getString(R.string.bps_ex_fav_key));
        Preference importFavourite = findPreference(getString(R.string.bps_im_fav_key));
        Preference exportAddWord = findPreference(getString(R.string.bps_ex_add_key));
        Preference importAddWord = findPreference(getString(R.string.bps_im_add_key));

        exportFavourite.setSummary("File saved on "+path+" directory");
        exportAddWord.setSummary("File saved on "+path +" directory");


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

            StorageChooser.Builder builder = new StorageChooser.Builder();
            builder.withActivity(getActivity());
            builder.withFragmentManager(getActivity().getFragmentManager());
            builder.withMemoryBar(true);
            builder.allowCustomPath(true);
            builder.setType(StorageChooser.FILE_PICKER);
            builder.setDialogTitle("Select file");

            //if path is not null then save that path as default path
            if (path != null){
                builder.withPredefinedPath(path);
            }

            StorageChooser chooser = builder.build();

            // Show dialog whenever you want by
            chooser.show();

            // get path that the user has chosen
            chooser.setOnSelectListener(path -> FileImportExportUtils.importFile(getContext(),path));


        } else {
            Toast.makeText(getContext(), "Something went wrong.Your storage is not readable." +
                            " Your storage option is different from others.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void writeFavouriteData() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            FileImportExportUtils.exportFileFavourite(getContext());

        } else {
            Toast.makeText(getContext(), "Something went wrong.Your storage is not writable." +
                            " Your storage option is different from others.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    //readWrite add word
    private void writeAddedWord() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            FileImportExportUtils.exportFileUser(getContext());
        } else {
            Toast.makeText(getContext(), "Something went wrong.Your storage is not writable." +
                            " Your storage option is different from others.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void readAddedWord() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {

            StorageChooser.Builder builder = new StorageChooser.Builder();
            builder.withActivity(getActivity());
            builder.withFragmentManager(getActivity().getFragmentManager());
            builder.withMemoryBar(true);
            builder.allowCustomPath(true);
            builder.setType(StorageChooser.FILE_PICKER);
            builder.setDialogTitle("Select file");

            //if path is not null then save that path as default path
            if (path != null){
                builder.withPredefinedPath(path);
            }

            StorageChooser chooser = builder.build();

            // Show dialog whenever you want by
            chooser.show();

            // get path that the user has chosen
            chooser.setOnSelectListener(path -> FileImportExportUtils.importFile(getContext(),path));

        } else {
            Toast.makeText(getContext(), "Something went wrong.Your storage is not readable." +
                            " Your storage option is different from others.",
                    Toast.LENGTH_SHORT).show();
        }
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
                    Toast.makeText(getContext(), "oh! you don't give permission to access storage" +
                            " To backup your data you need to grant permission", Toast.LENGTH_LONG).show();
                }
                break;

            case PERMISSIONS_REQUEST_READ_STORAGE_ADDED:

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    readAddedWord();

                } else {
                    Toast.makeText(getContext(), "oh! you don't give permission to access storage" +
                            " To backup your data you need to grant permission", Toast.LENGTH_LONG).show();
                }
                break;

            case PERMISSIONS_REQUEST_WRITE_STORAGE_FAVOURITE:

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    writeFavouriteData();

                } else {
                    Toast.makeText(getContext(), "oh! you don't give permission to access storage" +
                            " To backup your data you need to grant permission", Toast.LENGTH_LONG).show();
                }
                break;

            case PERMISSIONS_REQUEST_WRITE_STORAGE_ADDED:

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    writeAddedWord();

                } else {
                    Toast.makeText(getContext(), "oh! you don't give permission to access storage" +
                            " To backup your data you need to grant permission", Toast.LENGTH_LONG).show();
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }
    }

    //replaced by library

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        if (resultCode == RESULT_OK) {
//
//            Uri uri = data.getData();
//
//            switch (requestCode) {
//                case PICKFILE_RESULT_CODE_FAVOURITE_WORD:
//                    FileImportExportUtils.importFile(getContext(), ConstantUtils.SETTING_IMOUT_OPTION_FAVOUTITR,uri);
//                    break;
//
//                case PICKFILE_RESULT_CODE_ADDED_WORD:
//                    FileImportExportUtils.importFile(getContext(), ConstantUtils.SETTING_IMOUT_OPTION_USER,uri);
//                    break;
//        }
//
//
//        super.onActivityResult(requestCode, resultCode, data);
//    }
}
