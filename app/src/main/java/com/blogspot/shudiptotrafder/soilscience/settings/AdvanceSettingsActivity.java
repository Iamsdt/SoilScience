package com.blogspot.shudiptotrafder.soilscience.settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.blogspot.shudiptotrafder.soilscience.R;
import com.blogspot.shudiptotrafder.soilscience.theme.ThemeUtils;

public class AdvanceSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ThemeUtils.initialize(this);

        setContentView(R.layout.activity_advance_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.advanceSettingsToolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //buy calling android.R.id.home

        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
