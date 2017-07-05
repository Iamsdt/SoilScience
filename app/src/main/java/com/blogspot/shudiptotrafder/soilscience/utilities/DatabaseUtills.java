package com.blogspot.shudiptotrafder.soilscience.utilities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.TextUtils;

import com.blogspot.shudiptotrafder.soilscience.BuildConfig;
import com.blogspot.shudiptotrafder.soilscience.data.DataBaseProvider;
import com.blogspot.shudiptotrafder.soilscience.data.MainWordDBContract;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shudipto on 7/5/2017.
 */

public class DatabaseUtills{

    public static void addRemoteData(Activity context){

        boolean state = getRemoteConfigStatus(context);

        if (!state){
            return;
        }

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference pathReference = firebaseStorage.getReference().child("data/ssdata.txt");

        try {

            final File file = File.createTempFile("ssdata", "txt");

            pathReference.getFile(file)
                    .addOnSuccessListener(context,taskSnapshot -> {

                        Utility.showLog("Success storage");

                        try {

                            BufferedReader reader = new BufferedReader(new FileReader(file));

                            String line;

                            while ((line = reader.readLine()) != null) {
                                String[] strings = TextUtils.split(line, "=");

                                if (strings.length < 1) {
                                    continue;
                                }

                                //those data saved in a array
                                //first position for array is word
                                //second position for array is description

                                ContentValues values = new ContentValues();

                                //we use trim() for trim unexpected value
                                values.put(MainWordDBContract.Entry.COLUMN_WORD, strings[0].trim());
                                values.put(MainWordDBContract.Entry.COLUMN_DESCRIPTION, strings[1].trim());

                                Uri uri = context.getContentResolver().insert(MainWordDBContract.Entry.CONTENT_URI, values);

                                if (uri != null) {
                                    Utility.showLog("remote Data status:" + "successfull" + uri.toString());

                                }
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                            Utility.showLogThrowable(e.getMessage(),e);

                        }

                    })
                    .addOnFailureListener(context,e ->
                            Utility.showLogThrowable(e.getMessage(),e));


        } catch (Exception e) {
            e.printStackTrace();
            Utility.showLogThrowable("File exception",e);
        }
    }


    private static boolean getRemoteConfigStatus(Activity activity){

        boolean state;

        FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();

        FirebaseRemoteConfigSettings remoteSetting = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();

        remoteConfig.setConfigSettings(remoteSetting);

        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put(ConstantUtills.FB_REMOTE_STORAGE_KEY, false);

        remoteConfig.setDefaults(objectMap);

        final long cacheExpiration = 0; // 1 hour in seconds

        // If developer mode is enabled reduce cacheExpiration to 0 so that each fetch goes to the
        // server. This should not be used in release builds.

        final Task<Void> fetch = remoteConfig.fetch(cacheExpiration);

        fetch.addOnSuccessListener(activity,aVoid -> remoteConfig.activateFetched());
        fetch.addOnFailureListener(activity,e -> {
            //todo analytics data
        });

        state = remoteConfig.getBoolean(ConstantUtills.FB_REMOTE_STORAGE_KEY);

        return state;
    }

    /**
     * this methods for database initialize
     * it's only called for the first time of the app run
     * or update of any data base
     */
    public static void initializedDatabase(Context context) {

        DataBaseProvider provider = new DataBaseProvider(context);

        //SharedPreferences preferences for database initialize
        // for first time value
        SharedPreferences preferences = context.getSharedPreferences(ConstantUtills.DATABASE_INIT_SP_KEY,
                Context.MODE_PRIVATE);
        //sate of database is initialized or not
        boolean state = preferences.getBoolean(ConstantUtills.DATABASE_INIT_SP_KEY, false);

        SharedPreferences.Editor editor = preferences.edit();
        try {
            if (!state) {
                provider.loadWords();
                editor.putBoolean(ConstantUtills.DATABASE_INIT_SP_KEY, true);
                Utility.showLog("initializedDatabase called");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Utility.showLogThrowable("Error to initialized data", e);
            editor.putBoolean(ConstantUtills.DATABASE_INIT_SP_KEY, false);
        }
        editor.apply();
    }
}
