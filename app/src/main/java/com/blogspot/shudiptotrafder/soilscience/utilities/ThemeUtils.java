package com.blogspot.shudiptotrafder.soilscience.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.blogspot.shudiptotrafder.soilscience.R;

/**
 * Created by Shudipto on 6/19/2017.
 */

public class ThemeUtils {

    public static int getThemeId(Context context) {
        SharedPreferences preferences = context.
                getSharedPreferences(ConstantUtills.THEME_SP_KEY,
                        Context.MODE_PRIVATE);
        return preferences.getInt(ConstantUtills.THEME_SP_KEY, R.style.AppTheme_NoActionBar);
    }

    public static void initilize(Activity activity) {
        activity.setTheme(getThemeId(activity));
    }

}
