package com.blogspot.shudiptotrafder.soilscience;

import android.content.ContentValues;
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
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.blogspot.shudiptotrafder.soilscience.adapter.CustomCursorAdapter;
import com.blogspot.shudiptotrafder.soilscience.data.MainWordDBContract;
import com.blogspot.shudiptotrafder.soilscience.settings.SettingsActivity;
import com.blogspot.shudiptotrafder.soilscience.theme.ThemeUtils;
import com.blogspot.shudiptotrafder.soilscience.utilities.ConstantUtils;
import com.google.firebase.analytics.FirebaseAnalytics;

import static com.blogspot.shudiptotrafder.soilscience.data.MainWordDBContract.Entry.buildUriWithWord;

public class FavouriteActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
        CustomCursorAdapter.ClickListener {


    //adapter
    private CustomCursorAdapter mAdapter;

    //views
    private View noFavourite;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ThemeUtils.initialize(this);

        setContentView(R.layout.activity_favourite);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        recyclerView = (RecyclerView) findViewById(R.id.favouriteRecycleView);
        noFavourite = findViewById(R.id.no_favouriteLayout);

        LinearLayoutManager manager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(manager);

        mAdapter = new CustomCursorAdapter(this, this);

        recyclerView.setAdapter(mAdapter);


        //todo set a undo button for this

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.START|ItemTouchHelper.END) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                String word = (String) viewHolder.itemView.getTag();

                Uri uri = MainWordDBContract.Entry.buildUriWithWord(word);

                ContentValues values = new ContentValues();
                values.put(MainWordDBContract.Entry.COLUMN_FAVOURITE, false);
                int update = getContentResolver().update(uri, values, null, null);

                if (update != -1) {
                    Snackbar.make(viewHolder.itemView, "word removed from favourite",
                            Snackbar.LENGTH_SHORT).show();

                    mAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                    mAdapter.notifyItemChanged(viewHolder.getAdapterPosition()
                            , mAdapter.getItemCount());

                    getSupportLoaderManager().restartLoader(ConstantUtils.FAVOURITE_LOADER_ID, null, FavouriteActivity.this);
                }

            }
        }).attachToRecyclerView(recyclerView);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.favouriteFab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy > 1)
                    fab.hide();
                else if (dy < 1)
                    fab.show();
            }
        });


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getSupportLoaderManager().initLoader(ConstantUtils.FAVOURITE_LOADER_ID, null, this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(
                ConstantUtils.FAVOURITE_LOADER_ID, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.favourite,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home){
            onBackPressed();

        } else if (id == R.id.action_settings){
            startActivity(new Intent(this, SettingsActivity.class));

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {
            case ConstantUtils.FAVOURITE_LOADER_ID:

                String selection = MainWordDBContract.Entry.COLUMN_FAVOURITE + " = ? ";
                String[] selectionArg = new String[]{"1"};

                return new CursorLoader(this, MainWordDBContract.Entry.CONTENT_URI,
                        ConstantUtils.projectionOnlyWord,
                        selection, selectionArg, MainWordDBContract.Entry.COLUMN_WORD);

            default:
                throw new RuntimeException("Loader not initialize " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.getCount() > 0) {
            mAdapter.swapCursor(data);
            FirebaseAnalytics.getInstance(this).logEvent("Favourite_added",null);
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

