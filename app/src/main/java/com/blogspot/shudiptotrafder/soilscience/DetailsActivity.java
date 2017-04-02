package com.blogspot.shudiptotrafder.soilscience;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.blogspot.shudiptotrafder.soilscience.data.MainWordDBContract;

public class DetailsActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ID_DETAIL_LOADER = 321;
    private static final String[] projection = {
            MainWordDBContract.MainWordDBEntry.COLUMN_WORD,
            MainWordDBContract.MainWordDBEntry.COLUMN_DESCRIPTION
    };
    private static final int WORD_ID = 0;
    private static final int DESCRIPTION_ID = 1;
    private Uri mUri = null;
    private TextView wordTV;
    private TextView descriptionTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            mUri = getIntent().getData().normalizeScheme();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Uri can not null");
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.details_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getSupportLoaderManager().initLoader(ID_DETAIL_LOADER, null, this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(ID_DETAIL_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {
            case ID_DETAIL_LOADER:
                return new CursorLoader(this, mUri,
                        projection,
                        null, null, null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        boolean cursorHasValidateData = false;

        if (data != null && data.moveToFirst()) {
            cursorHasValidateData = true;
        }


        if (!cursorHasValidateData) {
            //nothing to display
            return;
        }

        String word = data.getString(WORD_ID);
        String description = data.getString(DESCRIPTION_ID);

        wordTV = (TextView) findViewById(R.id.details_word);
        descriptionTV = (TextView) findViewById(R.id.details_description);

        wordTV.setText(word);
        descriptionTV.setText(description);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
