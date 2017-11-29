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
import android.support.annotation.NonNull;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.widget.Toast;

import com.blogspot.shudiptotrafder.soilscience.R;
import com.blogspot.shudiptotrafder.soilscience.utilities.ConstantUtils;
import com.codekidlabs.storagechooser.StorageChooser;

import java.io.File;

public class AdvanceSettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int PERMISSIONS_REQUEST_READ_STORAGE = 0;

    private Preference changeDirPref;

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //if for re queries
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Add 'general' preferences, defined in the XML file
        addPreferencesFromResource(R.xml.pref_advance);

        changeDirPref = findPreference(getString(R.string.advance_dir_add_key));

        changeDirPref.setSummary(ConstantUtils.DEFAULT_PATH_STORAGE);

        changeDirPref.setOnPreferenceClickListener(preference -> {

            if (Build.VERSION.SDK_INT >= 23) {

                if (getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
                            , PERMISSIONS_REQUEST_READ_STORAGE);
                } else {
                    selectDir();
                }

            } else {
                selectDir();
            }

            return true;
        });
    }

    private void selectDir(){

        File file = new File(ConstantUtils.DEFAULT_PATH_STORAGE);

        if (!file.exists()){
            file.mkdirs();
        }

        SharedPreferences sp = getActivity()
                .getSharedPreferences(ConstantUtils.STORAGE_PATH_KEY, Context.MODE_PRIVATE);


        StorageChooser chooser = new StorageChooser.Builder()
                .withActivity(getActivity())
                .withFragmentManager(getActivity().getFragmentManager())
                .withMemoryBar(true)
                .allowCustomPath(true)
                .setDialogTitle("Select a Directory")
                .setType(StorageChooser.DIRECTORY_CHOOSER)
                .withPredefinedPath(file.getAbsolutePath())
                .actionSave(true)
                .withPreference(sp)
                .build();

        // Show dialog whenever you want by
        chooser.show();

        // get path that the user has chosen
        chooser.setOnSelectListener(this::refreshSummery);
    }

    private void refreshSummery(String s){
        if (changeDirPref != null){
            changeDirPref.setSummary(s);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_READ_STORAGE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Permission added
                selectDir();
            } else {
                Toast.makeText(getContext(), "oh! you don't give permission to access storage" +
                        ".To backup your data you need to grant permission", Toast.LENGTH_LONG).show();
            }
        }

    }

}