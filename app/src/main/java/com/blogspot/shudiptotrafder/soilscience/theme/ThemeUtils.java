package com.blogspot.shudiptotrafder.soilscience.theme;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.blogspot.shudiptotrafder.soilscience.R;
import com.blogspot.shudiptotrafder.soilscience.utilities.ConstantUtils;
import com.blogspot.shudiptotrafder.soilscience.utilities.Utility;

/**
 * Created by Shudipto on 6/19/2017.
 */

public class ThemeUtils {

    /**
     * This methods for select theme from
     * shared preference that saved in color activity
     *
     * @param activity to select theme
     */

    public static void initialize(Activity activity) {

        //find theme form shared preferences
        SharedPreferences preferences = activity.
                getSharedPreferences(ConstantUtils.THEME_SP_KEY,
                        Context.MODE_PRIVATE);

        //apply theme with id
        activity.setTheme(preferences.getInt(ConstantUtils.THEME_SP_KEY,
                R.style.AppTheme_NoActionBar));

        Utility.setNightMode(activity);
    }

}
