package com.blogspot.shudiptotrafder.soilscience.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.blogspot.shudiptotrafder.soilscience.BuildConfig;
import com.blogspot.shudiptotrafder.soilscience.R;
import com.blogspot.shudiptotrafder.soilscience.data.DataBaseProvider;

import java.io.IOException;

/**
 * Created by Shudipto on 6/6/2017.
 */

public class Utility {

//    private static boolean getNightModeEnabled(Context context) {
//
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
//
//        return preferences.getBoolean(context.getString(R.string.switchKey),false);
//    }

    public static void setNightMode(Context context) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        boolean isEnabled = preferences.getBoolean(context.getString(R.string.switchKey), false);

        if (isEnabled) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public static int getTextSize(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        String s = preferences.getString(context.getString(R.string.textSizeKey),
                context.getString(R.string.sTextModerateValue));

        return Integer.parseInt(s);

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
                showLog("initializedDatabase called");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showLogThrowable("Error to initialized data", e);
            editor.putBoolean(ConstantUtills.DATABASE_INIT_SP_KEY, false);
        }
        editor.apply();
    }

    /**
     * This methods show log error message with throwable
     *
     * @param message String show on log
     */
    public static void showLog(String message) {

        final String TAG = "Utility";

        if (BuildConfig.DEBUG) {
            Log.e(TAG, message);
        }
    }

    /**
     * This methods show log error message with throwable
     *
     * @param message String show on log
     * @param t       throwable that's show on log
     */

    public static void showLogThrowable(String message, Throwable t) {

        final String TAG = "Utility";

        if (BuildConfig.DEBUG) {
            Log.e(TAG, message, t);
        }
    }

}
