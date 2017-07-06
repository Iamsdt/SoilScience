package com.blogspot.shudiptotrafder.soilscience.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.blogspot.shudiptotrafder.soilscience.BuildConfig;
import com.blogspot.shudiptotrafder.soilscience.R;
import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by Shudipto on 6/6/2017.
 */

public class Utility {

    public static boolean isUploadEnabled(Context context) {

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        //TOdo add settings
        boolean isEnabled = preferences.getBoolean(context.getString(R.string.switchKey),
                true);


        if (!isNetworkAvailable(context)) {
            isEnabled = false;
        }

        return isEnabled;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);

        NetworkInfo info = manager.getActiveNetworkInfo();

        return info != null && info.isConnectedOrConnecting();
    }

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

        final String TAG = "Utility";

        if (BuildConfig.DEBUG) {
            Log.e(TAG, message);
        }


    }

    public static void setAnalyticsData(Context context, String type, String message) {

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);

        Bundle b = new Bundle();
        b.putString(type, message);

        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, b);

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
