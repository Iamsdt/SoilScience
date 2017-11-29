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

package com.blogspot.shudiptotrafder.soilscience.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.blogspot.shudiptotrafder.soilscience.BuildConfig;
import com.blogspot.shudiptotrafder.soilscience.R;
import com.blogspot.shudiptotrafder.soilscience.data.MainWordDBContract;
import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by Shudipto Trafder.
 * on 6/6/2017
 */

public class Utility {

    /**Check User settings for upload is enabled or not
     * @return status*/
    public static boolean isUploadEnabled(Context context) {

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        return !preferences.getBoolean(
                context.getString(R.string.switchShare),true);
    }
    /**
     * Checking network is available or not
     * @return network state*/
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);

        NetworkInfo info = null;
        if (manager != null) {
            info = manager.getActiveNetworkInfo();
        }

        return info != null && info.isConnectedOrConnecting();
    }

    /**
     * set nightMode to activity
     * Check user settings first
     *
     * @param context for get shared preference
     */
    public static void setNightMode(Context context) {

        SharedPreferences sharedPreferences =
                context.getSharedPreferences(ConstantUtils.NIGHT_MODE_SP_KEY, Context.MODE_PRIVATE);
        boolean isEnabled = sharedPreferences.getBoolean(ConstantUtils.NIGHT_MODE_VALUE_KEY, false);

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

    //Set analytics data to firebase
    public static void setAnalyticsData(Context context, String type, String message) {

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);

        Bundle b = new Bundle();
        b.putString(type, message);

        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM_LIST, b);

    }

    /**
     * This methods show log error message with throwable
     *
     * @param message String show on log
     * @param t throwable that's show on log
     */
    public static void showLogThrowable(String message, Throwable t) {

        final String TAG = "Utility";
        //TODO add crash report data

        if (BuildConfig.DEBUG) {
            Log.e(TAG, message, t);
        }
    }

    /**
     * Method for getting a word description
     * @param context to access content proider
     * @param mUri uri for word path*/
    public static String getWordWithDes(Context context,Uri mUri){

        final String[] projection = {
                MainWordDBContract.Entry.COLUMN_DESCRIPTION
        };

        String description = null;

        Cursor cursor = context.getContentResolver()
                .query(mUri,projection,null,null,null);

        if (cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();
            do {
                description = cursor.getString(0);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        return description;
    }

}
