package com.blogspot.shudiptotrafder.soilscience.theme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;

import com.blogspot.shudiptotrafder.soilscience.R;
import com.blogspot.shudiptotrafder.soilscience.adapter.ColorAdapter;
import com.blogspot.shudiptotrafder.soilscience.utilities.ConstantUtills;
import com.blogspot.shudiptotrafder.soilscience.utilities.ThemeUtils;

import java.util.ArrayList;

public class ColorActivity extends AppCompatActivity implements
        ColorAdapter.ColorClickListener {

    private static final String EXTRA_TITLE = "ColorsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(ThemeUtils.getThemeId(this));

        setContentView(R.layout.activity_color);
        Toolbar toolbar = (Toolbar) findViewById(R.id.color_toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.color_recyclerView);

        LinearLayoutManager manager = new LinearLayoutManager
                (this, LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);

        ColorAdapter colorAdapter = new ColorAdapter(this, getColorIds(), this);
        recyclerView.setAdapter(colorAdapter);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    private ArrayList<ThemesContract> getColorIds() {

        ArrayList<ThemesContract> arrayList = new ArrayList<>();

        // FIXME: 6/19/2017 add more theme
        //fill array with styles ids
        arrayList.add(new ThemesContract("Default", R.style.AppTheme_NoActionBar));
        arrayList.add(new ThemesContract("Amber", R.style.amber));
        arrayList.add(new ThemesContract("Amber Dark", R.style.amber_dark));
        arrayList.add(new ThemesContract("Purple Dark", R.style.purple_dark));

        return arrayList;
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

        // FIXME: 6/19/2017 add one array this don't use two arraylist
        ArrayList<ThemesContract> arrayList = getColorIds();

        ThemesContract themeCont = arrayList.get(id);

        SharedPreferences preferences = getSharedPreferences(ConstantUtills.THEME_SP_KEY, Context.MODE_PRIVATE);


        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt(ConstantUtills.THEME_SP_KEY, themeCont.getId());

        editor.apply();

        Intent restartIntent = new Intent(this, ColorActivity.class);
        setResult(RESULT_OK);
        finish();
        startActivity(restartIntent);
        overridePendingTransition(0, 0);
    }

    public static Intent createIntent(Context context, @Nullable String title) {
        Intent intent = new Intent(context, ColorActivity.class);
        if (!TextUtils.isEmpty(title)) intent.putExtra(EXTRA_TITLE, title);
        return intent;
    }

}
