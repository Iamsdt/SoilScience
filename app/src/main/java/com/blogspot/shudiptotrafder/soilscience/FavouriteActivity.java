package com.blogspot.shudiptotrafder.soilscience;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.blogspot.shudiptotrafder.soilscience.adapter.CustomCursorAdapter;
import com.blogspot.shudiptotrafder.soilscience.data.MainWordDBContract;

import static com.blogspot.shudiptotrafder.soilscience.data.MainWordDBContract.Entry.buildUriWithWord;

public class FavouriteActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
        CustomCursorAdapter.ClickListener{

    //Loader id
    private static final int LOADER_ID = 122;

    //adapter
    CustomCursorAdapter mAdapter;

    //views
    private View noFavourite;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.favouriteRecycleView);
        noFavourite = findViewById(R.id.no_favouriteLayout);

        LinearLayoutManager manager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL,false);

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(manager);

        mAdapter = new CustomCursorAdapter(this,this);

        recyclerView.setAdapter(mAdapter);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.favouriteFab);
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

        getSupportLoaderManager().initLoader(LOADER_ID,null,this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(LOADER_ID,null,this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id){
            case LOADER_ID:

                String selection = MainWordDBContract.Entry.COLUMN_FAVOURITE+ " = ? ";
                String[] selectionArg = new String[]{"1"};

                return new CursorLoader(this, MainWordDBContract.Entry.CONTENT_URI,
                        MainActivity.projection,
                        selection,selectionArg, MainWordDBContract.Entry._ID);

            default:
                throw new RuntimeException("Loader not initialize "+id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.getCount() > 0){
            mAdapter.swapCursor(data);
        } else {
            recyclerView.setVisibility(View.GONE);
            noFavourite.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onItemClickListener(String word) {
        Intent intent = new Intent(FavouriteActivity.this, DetailsActivity.class);
        Uri wordUri = buildUriWithWord(word);
        intent.setData(wordUri);
        startActivity(intent);
    }
}

