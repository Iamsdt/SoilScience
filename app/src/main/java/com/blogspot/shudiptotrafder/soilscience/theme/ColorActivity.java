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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.blogspot.shudiptotrafder.soilscience.R;
import com.blogspot.shudiptotrafder.soilscience.adapter.ColorAdapter;
import com.blogspot.shudiptotrafder.soilscience.utilities.ConstantUtils;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;

public class ColorActivity extends AppCompatActivity implements
        ColorAdapter.ColorClickListener {

    private ArrayList<ThemesContract> themes;

    //to support vector drawables for lower api
    static {
        //complete add vector drawable support
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ThemeUtils.initialize(this);

        setContentView(R.layout.activity_color);
        Toolbar toolbar = findViewById(R.id.color_toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = findViewById(R.id.color_recyclerView);

        LinearLayoutManager manager = new LinearLayoutManager
                (this, LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);

        //fill themes array with id before send to colorAdapter
        fillThemeIds();

        ColorAdapter colorAdapter = new ColorAdapter(this, themes, this);
        recyclerView.setAdapter(colorAdapter);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    /**
     * This method is for add new theme in arrayList
     * array list contain theme name and it's id
     */

    private void fillThemeIds() {

        themes = new ArrayList<>();

        // complete: 6/19/2017 add more theme
        //fill array with styles ids
        themes.add(new ThemesContract("Default", R.style.AppTheme_NoActionBar));
        themes.add(new ThemesContract("Amber", R.style.amber_dark));
        themes.add(new ThemesContract("Purple", R.style.purple_dark));
        themes.add(new ThemesContract("Orange", R.style.orange));
        themes.add(new ThemesContract("Cyan", R.style.cyan));
        themes.add(new ThemesContract("Deep Orange", R.style.deeporange));
        themes.add(new ThemesContract("Green", R.style.green));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //buy calling android.R.id.home

        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onColorItemClick(int id) {

        // complete: 6/19/2017 add one array this don't use two array list

        ThemesContract themeCont = themes.get(id);

        SharedPreferences preferences = getSharedPreferences(ConstantUtils.THEME_SP_KEY, Context.MODE_PRIVATE);


        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt(ConstantUtils.THEME_SP_KEY, themeCont.getId());

        editor.apply();

        FirebaseAnalytics.getInstance(this).logEvent("Theme_changed",null);

        Intent restartIntent = new Intent(this, ColorActivity.class);
        setResult(RESULT_OK);
        finish();
        startActivity(restartIntent);
        overridePendingTransition(0, 0);
    }

    public static Intent createIntent(Context context) {
        return new Intent(context, ColorActivity.class);
    }

}
