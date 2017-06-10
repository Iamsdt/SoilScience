package com.blogspot.shudiptotrafder.soilscience;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.blogspot.shudiptotrafder.soilscience.adapter.CustomCursorAdapter;
import com.blogspot.shudiptotrafder.soilscience.data.DataBaseProvider;
import com.blogspot.shudiptotrafder.soilscience.data.MainWordDBContract;
import com.blogspot.shudiptotrafder.soilscience.settings.SettingsActivity;
import com.blogspot.shudiptotrafder.soilscience.utilities.Utility;

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

    public static final String[] projection = new String[]
            {MainWordDBContract.Entry.COLUMN_WORD};

    //for words index
    public static final int INDEX_WORD = 0;

    static {
        //complete add vector drawable support
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setNightMode();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initializedDatabase();

        //progress dialog for force wait user for database ready
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait a while");
        progressDialog.setCancelable(false);
        progressDialog.show();

        //assign view
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.mainRecycleView);

        final LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(manager);

        mAdapter = new CustomCursorAdapter(this, this);

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

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.main_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);

                String s = mAdapter.getRandomWord();

                Uri wordUri = MainWordDBContract.Entry.buildUriWithWord(s);
                intent.setData(wordUri);
                startActivity(intent);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 1)
                    fab.hide();
                else if (dy < 1)
                    fab.show();
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        //deprecated
        //drawer.setDrawerListener(toggle);
        //fix in this way
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }



    @Override
    protected void onStart() {
        super.onStart();
        if (!state) {
            initializedDatabase();
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                //Toast.makeText(SettingActivity.this, "change deced", Toast.LENGTH_SHORT).show();
                if (key.equals(getString(R.string.switchKey))) {
                    recreate();

                }

                if (key.equalsIgnoreCase(getString(R.string.textSizeKey))) {
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * this methods for database initialize
     * it's only called for the first time of the app run
     * or update of any data base
     */
    private void initializedDatabase() {

        DataBaseProvider provider = new DataBaseProvider(MainActivity.this);

        if (!state) {
            SharedPreferences.Editor editor = preferences.edit();
            try {
                provider.loadWords();
                editor.putBoolean(key, true);
                sle("initializedDatabase called");
            } catch (IOException e) {
                e.printStackTrace();
                slet("Error to initialized data", e);
                editor.putBoolean(key, false);
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

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_favourite) {
            startActivity(new Intent(this, FavouriteActivity.class));

        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));

        } else if (id == R.id.nav_add) {
            startActivity(new Intent(this, UserAddActivity.class));

        } else if (id == R.id.nav_share) {
            showDummyText();

        } else if (id == R.id.nav_send) {
            showDummyText();

        } else if (id == R.id.nav_developer) {
            showDummyText();

        } else if (id == R.id.nav_copyright) {
            showDummyText();

        } else if (id == R.id.nav_about) {
            showDummyText();

        } else if (id == R.id.nav_legal) {
            showDummyText();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void showDummyText() {
        Toast.makeText(this, "Not available yet", Toast.LENGTH_SHORT).show();
    }

    //for option menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_search:
                Toast.makeText(this, "Text Search option is Coming soon", Toast.LENGTH_SHORT).show();
                //openSearch();
                return true;
            case R.id.action_stt:
                Toast.makeText(this, "Voice Search option is Coming soon", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //for loader
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, MainWordDBContract.Entry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        // Update the data that the adapter uses to create ViewHolders
        Log.e("Data", String.valueOf(data.getCount()));
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    /**
     * onClick listener for recycler view
     * called if click any item on recycler view
     *
     * @param word is the selected word from data base
     */
    @Override
    public void onItemClickListener(String word) {
        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        Uri wordUri = MainWordDBContract.Entry.buildUriWithWord(word);
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

    //set night mode
    private void setNightMode() {

        boolean isEnabled = Utility.getNightModeEnabled(this);

        if (isEnabled) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

}
