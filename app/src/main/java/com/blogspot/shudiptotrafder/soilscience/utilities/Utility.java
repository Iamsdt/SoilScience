package com.blogspot.shudiptotrafder.soilscience.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import com.blogspot.shudiptotrafder.soilscience.R;

/**
 * Created by Shudipto on 6/6/2017.
 */

public class Utility {

    public static boolean getNightModeEnabled(Context context) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        return preferences.getBoolean(context.getString(R.string.switchKey),false);
    }

    public static int getTextSize(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        String s = preferences.getString(context.getString(R.string.textSizeKey),
                context.getString(R.string.sTextModerateValue));

        return Integer.parseInt(s);

    }
}
