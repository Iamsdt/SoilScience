package com.blogspot.shudiptotrafder.soilscience;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.blogspot.shudiptotrafder.soilscience.theme.ThemeUtils;

public class DeveloperActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ThemeUtils.initialize(this);

        setContentView(R.layout.activity_developer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.developer_fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }


    //left in this project
    // complete: 6/7/2017 add theme change option
    // complete: 6/7/2017 add search view
    // complete: 6/7/2017 add voice search view
    // complete: 6/7/2017 add random search view
    
    // complete: 6/7/2017 add app intro
    // TODO: 6/7/2017 add item animator 

    //advance
    //complete fill developer with animation

    //database
    // complete: 6/16/2017 Firebase database
    // complete: 6/16/2017 user add word will be real time database

    // TODO: 6/16/2017 favourite swap will show a dialog
    // complete: 6/16/2017 night mode move from settings to navigation drawer

    // complete: 8/4/2017 if user open this app for first time then show a import dialog
    // TODO: 8/4/2017 Create a file import chooser dialog

}
