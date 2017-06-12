package com.blogspot.shudiptotrafder.soilscience;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
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
import com.blogspot.shudiptotrafder.soilscience.data.MainWordDBContract;
import com.blogspot.shudiptotrafder.soilscience.settings.SettingsActivity;
import com.blogspot.shudiptotrafder.soilscience.utilities.ConstantUtills;
import com.blogspot.shudiptotrafder.soilscience.utilities.Utility;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        CustomCursorAdapter.ClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    //loaders id that initialized that's one loader is running
    private static final int TASK_LOADER_ID = 0;

    //speech to text
    private static final int REQ_CODE_SPEECH_INPUT = 100;


    //it's also show when data base are loading
    ProgressDialog progressDialog;

    private CustomCursorAdapter mAdapter;

    //selected column form database
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
        super.onCreate(savedInstanceState);

        Utility.setNightMode(this);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //progress dialog for force wait user for database ready
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait a while");
        progressDialog.setCancelable(false);

        //assign view
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.mainRecycleView);
        final LinearLayoutManager manager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);

        mAdapter = new CustomCursorAdapter(this, this);
        recyclerView.setAdapter(mAdapter);

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

        //fab hide with recycler view scroll
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
        //SharedPreferences for database initializing state
        // for first time value

        SharedPreferences preferences = getSharedPreferences(
                ConstantUtills.DATABASE_INIT_SP_KEY, MODE_PRIVATE);
        //sate of database is initialized or not
        boolean state = preferences.getBoolean(
                ConstantUtills.DATABASE_INIT_SP_KEY, false);

        if (!state) {
            progressDialog.show();
            Utility.initializedDatabase(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                //Toast.makeText(SettingActivity.this, "change deced", Toast.LENGTH_SHORT).show();
                if (key.equals(getString(R.string.switchKey))) {
                    sle("recreated");
                    recreate();
                }

                if (key.equalsIgnoreCase(getString(R.string.textSizeKey))) {
                    mAdapter.notifyDataSetChanged();
                }
            }
        });

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
                //Toast.makeText(this, "Voice Search option is Coming soon", Toast.LENGTH_SHORT).show();
                askSpeechInput();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Showing google speech input dialog

    private void askSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speak your desire word");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            a.printStackTrace();
            slet("Activity not found", a);
            Toast.makeText(this, "Sorry Speech To Text is not " +
                    "supported in your device", Toast.LENGTH_SHORT).show();
        }
    }

    // Receiving speech input

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    boolean contain = mAdapter.getAllWord().contains(result.get(0).toUpperCase());

                    if (contain){
                        Uri uri = MainWordDBContract.Entry.buildUriWithWord(result.get(0));

                        Intent intent = new Intent(MainActivity.this,
                                DetailsActivity.class);
                        intent.setData(uri);

                        startActivity(intent);

                    } else {
                        Toast.makeText(this, "Sorry word not found", Toast.LENGTH_SHORT).show();
                    }

                }

                break;
            }

        }
    }

    //for loader
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, MainWordDBContract.Entry.CONTENT_URI, projection, null, null,
                MainWordDBContract.Entry.COLUMN_WORD);
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

}
