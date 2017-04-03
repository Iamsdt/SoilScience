package com.blogspot.shudiptotrafder.soilscience;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.blogspot.shudiptotrafder.soilscience.adapter.CustomCursorAdapter;
import com.blogspot.shudiptotrafder.soilscience.data.DataBaseProvider;
import com.blogspot.shudiptotrafder.soilscience.data.MainWordDBContract;

import java.io.IOException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        CustomCursorAdapter.ClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    //loaders id that initialized that's one loader is running
    private static final int TASK_LOADER_ID = 0;
    //sp key
    private final String key = "initialized";
    SharedPreferences preferences;
    //it's also show when data base are loading
    ProgressDialog progressDialog;
    private CustomCursorAdapter mAdapter;
    //its indicate that's database initialized or not
    private boolean state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initializedDatabase();

        //progress dialog for force wait user for database ready
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait a while");
        progressDialog.setCancelable(false);

        //assign view
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.mainRecycleView);

        LinearLayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(manager);

        mAdapter = new CustomCursorAdapter(this);

        recyclerView.setAdapter(mAdapter);

        //SharedPreferences preferences for database initialize
        // for first time value
        preferences = getSharedPreferences(key, MODE_PRIVATE);
        //sate of database is initialized or not
        state = preferences.getBoolean(key, false);

        /*
         Ensure a loader is initialized and active. If the loader doesn't already exist, one is
         created, otherwise the last created loader is re-used.
         */
        getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.main_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //deprecated
        //drawer.setDrawerListener(toggle);
        //fix in this way
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * this methods for database initialize
     * it's only called for the first time of the app run
     * or update of any data base
     */
    private void initializedDatabase() {

        DataBaseProvider provider = new DataBaseProvider(MainActivity.this);

        if (!state){
            SharedPreferences.Editor editor = preferences.edit();
            try {
                provider.loadWords();
                editor.putBoolean(key,true);
                sle("initializedDatabase called");
            } catch (IOException e) {
                e.printStackTrace();
                slet("Error to initialized data",e);
                editor.putBoolean(key,false);
            }
            editor.apply();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        // re-queries for all tasks
        getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //for loader
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {

            // Initialize a Cursor, this will hold all the task data
            Cursor wordCursor = null;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                //if database is not initialize then show a progress dialog
                if (!state) {
                    if (progressDialog != null) {
                        progressDialog.show();
                    }
                }

                if (wordCursor != null && wordCursor.getCount() > 0){
                    // Delivers any previously loaded data immediately
                    deliverResult(wordCursor);
                } else {
                    // Force a new load
                    forceLoad();
                }

            }

            // loadInBackground() performs asynchronous loading of data
            @Override
            public Cursor loadInBackground() {

                initializedDatabase();

                return getContentResolver().query(MainWordDBContract.MainWordDBEntry.CONTENT_URI,
                        new String[]{MainWordDBContract.MainWordDBEntry.COLUMN_WORD},null,null,MainWordDBContract.MainWordDBEntry._ID + " ASC");
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(Cursor data) {
                wordCursor = data;
                super.deliverResult(data);
            }

        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        // Update the data that the adapter uses to create ViewHolders
        Log.e("Data",String.valueOf(data.getCount()));
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    /** onClick listener for recycler view
     * called if click any item on recycler view
     * @param word is the selected word from data base
     */
    @Override
    public void onItemClickListener(String word) {
        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        Uri wordUri = MainWordDBContract.MainWordDBEntry.buildUriWithWord(word);
        intent.setData(wordUri);
        startActivity(intent);
    }

    /**
     * log message methods that's display log only debug mode
     *
     * @param string message that to display
     */
    private static void sle(String string) {
        //show log with error message
        //if debug mode enable
        String Tag = "MainActivity";

        if (BuildConfig.DEBUG) {
            Log.e(Tag, string);
        }
    }

    /**
     * log message methods that's display log only debug mode
     *
     * @param s message that to display
     * @param t throwable that's throw if exception happen
     */
    private static void slet(String s, Throwable t) {
        //show log with error message with throwable
        //if debug mode enable
        String Tag = "MainActivity";

        if (BuildConfig.DEBUG) {
            Log.e(Tag, s, t);
        }
    }
    
}
