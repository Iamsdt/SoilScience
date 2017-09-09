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

package com.blogspot.shudiptotrafder.soilscience.theme;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.blogspot.shudiptotrafder.soilscience.R;
import com.blogspot.shudiptotrafder.soilscience.utilities.ConstantUtils;
import com.blogspot.shudiptotrafder.soilscience.utilities.Utility;

/**
 * Created by Shudipto Trafder.
 * on 6/19/2017
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
