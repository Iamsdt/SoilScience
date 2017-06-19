package com.blogspot.shudiptotrafder.soilscience.singleton;

import android.app.Application;
import android.preference.PreferenceManager;

import com.blogspot.shudiptotrafder.soilscience.R;
import com.ftinc.scoop.Scoop;

/**
 * Created by Shudipto Trafder on 6/18/2017.
 */

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Scoop.waffleCone()
                .addFlavor("Default", R.style.Theme_Scoop_Light, true)
                .addFlavor(" 1", R.style.Theme_Scoop_Alt1)
                .addFlavor("Alternate 2", R.style.Theme_Scoop_Alt2)
                .addFlavor("Orange", R.style.deepOrange_scoop)
                .setSharedPreferences(PreferenceManager.getDefaultSharedPreferences(this))
                .initialize();

    }
}
