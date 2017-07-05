package com.blogspot.shudiptotrafder.soilscience.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.blogspot.shudiptotrafder.soilscience.BuildConfig;
import com.blogspot.shudiptotrafder.soilscience.R;

/**
 * Created by Shudipto on 6/6/2017.
 */

public class Utility {

    /**
     * set nightMode to activity
     * Check user settings first
     *
     * @param context for get shared preference
     */
    public static void setNightMode(Context context) {

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        boolean isEnabled = preferences.getBoolean(context.getString(R.string.switchKey),
                false);

        if (isEnabled) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    /**
     * Get text size from user settings
     *
     * @param context access Shared preference
     * @return text size
     */
    public static int getTextSize(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        String s = preferences.getString(context.getString(R.string.textSizeKey),
                context.getString(R.string.sTextModerateValue));

        return Integer.parseInt(s);

    }

    /**
     * This methods show log error message with throwable
     *
     * @param message String show on log
     */
    public static void showLog(String message) {

        //TODO add analytics data

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
        //TODO add crash report data

        if (BuildConfig.DEBUG) {
            Log.e(TAG, message, t);
        }
    }

}
