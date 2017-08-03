package com.blogspot.shudiptotrafder.soilscience.data;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.blogspot.shudiptotrafder.soilscience.BuildConfig;
import com.blogspot.shudiptotrafder.soilscience.R;
import com.blogspot.shudiptotrafder.soilscience.utilities.ConstantUtils;
import com.blogspot.shudiptotrafder.soilscience.utilities.Utility;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shudipto Trafder on 7/5/2017.
 */

public class DatabaseUtils {

    public static boolean checkUploadLeft(Context context) {

        if (!Utility.isUploadEnabled(context)) {
            return false;
        }

        boolean state = false;

        Cursor cursor = context.getContentResolver().query(
                MainWordDBContract.Entry.CONTENT_URI,
                new String[]{MainWordDBContract.Entry.COLUMN_UPLOAD},
                MainWordDBContract.Entry.COLUMN_UPLOAD + " =? ",
                new String[]{"0"},
                null);

        if (cursor != null && cursor.getCount() > 0) {
            state = true;
        }

        if (cursor != null) {
            cursor.close();
        }


        return state;
    }

    /**
     * This method for add data from remote
     * we add new data into a file and change remote config
     * so add add a temp file and from that file we add this data
     * into database
     *
     * @param context for access content resolver
     */

    public static void addRemoteData(Context context) {

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference pathReference = firebaseStorage.getReference().child("data/ssdata.txt");

        try {

            final File file = File.createTempFile("ssdata", "txt");

            pathReference.getFile(file)
                    .addOnSuccessListener(taskSnapshot -> {
                        try {

                            BufferedReader reader = new BufferedReader(new FileReader(file));

                            String line;

                            while ((line = reader.readLine()) != null) {

                                String[] strings = TextUtils.split(line, "=");

                                boolean check = checkDataExist(context, strings[0].trim());

                                if (check) {
                                    Utility.showLog("loop continue");
                                    continue;
                                }

                                //those data saved in a array
                                //first position for array is word
                                //second position for array is description
                                ContentValues values = new ContentValues();

                                //we use trim() for trim unexpected value
                                values.put(MainWordDBContract.Entry.COLUMN_WORD, strings[0].trim());
                                values.put(MainWordDBContract.Entry.COLUMN_DESCRIPTION, strings[1].trim());

                                //context.getContentResolver().insert(MainWordDBContract.Entry.CONTENT_URI, values);

                                Uri uri = context.getContentResolver().insert(MainWordDBContract.Entry.CONTENT_URI, values);

                                if (uri != null) {
                                    //Utility.showLog("remote Data status:" + "successfull" + uri.toString());
                                    Utility.setAnalyticsData(context, "remote storage",
                                            "remote data add into database");
                                }
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                            //TODO crash report
                            Utility.showLogThrowable(e.getMessage(), e);

                        }

                    })
                    .addOnFailureListener(e ->
                            //TODO crash report
                            Utility.showLogThrowable(e.getMessage(), e));


        } catch (Exception e) {
            e.printStackTrace();
            //TODO crash report
            Utility.showLogThrowable("File exception", e);
        }
    }

    /**
     * Check data  check word is already exist or not
     *
     * @param context for access content resolver
     * @param word    check exist of not
     * @return result of data existence
     */

    public static boolean checkDataExist(Context context, String word) {

        boolean status = false;

        Uri uri = MainWordDBContract.Entry.buildUriWithWord(word);

        Utility.showLog(uri.toString());

        Cursor cursor = context.getContentResolver().query(uri,
                ConstantUtils.projectionOnlyWord, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            status = true;
            Utility.showLog("check data status:" + true);
            Utility.setAnalyticsData(context, "Remote_Storage_data_existence",
                    "Remote data already exist");
        }

        if (cursor != null) {
            cursor.close();
        }

        Utility.showLog("check data status:" + status);
        return status;
    }

    /**
     * This methods for check status of database
     * we set it from firebase
     * if we saved new data then we set it from firebase
     *
     * @param activity from where activity
     * @return status true or false
     */
    public static boolean getRemoteConfigStatus(Activity activity) {

        boolean state;

        FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();

        FirebaseRemoteConfigSettings remoteSetting = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();

        remoteConfig.setConfigSettings(remoteSetting);

        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put(ConstantUtils.FB_REMOTE_CONFIG_STORAGE_KEY, false);

        remoteConfig.setDefaults(objectMap);

        long cacheExpiration = 3600; // 1 hour in seconds

        if (remoteSetting.isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        // If developer mode is enabled reduce cacheExpiration to 0 so that each fetch goes to the
        // server. This should not be used in release builds.

        final Task<Void> fetch = remoteConfig.fetch(cacheExpiration);

        fetch.addOnSuccessListener(activity, aVoid -> remoteConfig.activateFetched());
        fetch.addOnFailureListener(activity, e -> {
            //todo crash report
            Utility.showLogThrowable("Remote config data failed", e);
        });

        state = remoteConfig.getBoolean(ConstantUtils.FB_REMOTE_CONFIG_STORAGE_KEY);

        Utility.showLog("StateData:" + state);

        return state;
    }

    /**
     * this methods for database initialize
     * it's only called for the first time of the app run
     * or update of any data base
     */
    public static void initializedDatabase(Context context) {

        //SharedPreferences preferences for database initialize
        // for first time value
        SharedPreferences preferences = context.getSharedPreferences(ConstantUtils.DATABASE_INIT_SP_KEY,
                Context.MODE_PRIVATE);
        //sate of database is initialized or not
        boolean state = preferences.getBoolean(ConstantUtils.DATABASE_INIT_SP_KEY, false);

        SharedPreferences.Editor editor = preferences.edit();

        if (!state) {
            loadWords(context);
            editor.putBoolean(ConstantUtils.DATABASE_INIT_SP_KEY, true);
            Utility.showLog("initializedDatabase called");
            Utility.setAnalyticsData(context, "Offline_database",
                    "created successfully");
        }

        editor.apply();
    }

    /**
     * methods for load words in database
     * with content provider
     */

    private static void loadWords(Context context) {

        final Resources resources = context.getResources();
        //resource i
        int rawId = R.raw.ssnewdb;

        InputStream stream = resources.openRawResource(rawId);

        //read data

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {

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

                //Uri uri = context.getContentResolver().insert(MainWordDBContract.Entry.CONTENT_URI, values);
                context.getContentResolver().insert(MainWordDBContract.Entry.CONTENT_URI, values);

//                if (uri != null) {
//                    //sle("Data status:" + "successfull");
//                }

            }
        } catch (IOException e) {
            //TODO crash report
            Utility.showLogThrowable("Database add error", e);
        }

    }
}
