package com.blogspot.shudiptotrafder.soilscience;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.blogspot.shudiptotrafder.soilscience.adapter.CustomCursorAdapter;
import com.blogspot.shudiptotrafder.soilscience.data.MainWordDBContract;
import com.blogspot.shudiptotrafder.soilscience.settings.SettingsActivity;
import com.blogspot.shudiptotrafder.soilscience.theme.ColorActivity;
import com.blogspot.shudiptotrafder.soilscience.utilities.ConstantUtils;
import com.blogspot.shudiptotrafder.soilscience.theme.ThemeUtils;
import com.blogspot.shudiptotrafder.soilscience.utilities.Utility;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Locale;

import br.com.mauker.materialsearchview.MaterialSearchView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        CustomCursorAdapter.ClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int THEME_CHANGER_REQUEST_CODE = 111;

    //recycler view adapter
    private CustomCursorAdapter mAdapter;

    //material search view
    private MaterialSearchView searchView;

    //floating action button
    private FloatingActionButton fab;


    //to support vector drawables for lower api
    static {
        //complete add vector drawable support
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Utility.setNightMode(this);
        ThemeUtils.initialize(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //assign view

        searchView = (MaterialSearchView) findViewById(R.id.search_view);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.mainRecycleView);
        final LinearLayoutManager manager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);

        mAdapter = new CustomCursorAdapter(this, this);
        recyclerView.setAdapter(mAdapter);


        //set all search action
        setAllSearchOption();

        /*
         Ensure a loader is initialized and active. If the loader doesn't already exist, one is
         created, otherwise the last created loader is re-used.
         */
        getSupportLoaderManager().initLoader(ConstantUtils.MAIN_LOADER_ID, null, this);

        //todo add circular review animation with fab
        fab = (FloatingActionButton) findViewById(R.id.main_fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, DetailsActivity.class);

            String s = mAdapter.getRandomWord();

            Uri wordUri = MainWordDBContract.Entry.buildUriWithWord(s);

            intent.setData(wordUri);
            startActivity(intent);
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

        FirebaseAnalytics.getInstance(this).logEvent("Main_activity_started",null);
    }

    @Override
    protected void onResume() {
        super.onResume();

        searchView.activityResumed();

        // re-queries for all tasks
        getSupportLoaderManager().restartLoader(ConstantUtils.MAIN_LOADER_ID, null, this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else if (searchView.isOpen()) {
            searchView.closeSearch();

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

        } else if (id == R.id.nav_choose_theme) {
            startActivityForResult(ColorActivity
                    .createIntent(this), THEME_CHANGER_REQUEST_CODE);

        } else if (id == R.id.nav_add) {
            startActivity(new Intent(this, UserAddActivity.class));

        } else if (id == R.id.nav_share) {
            showDummyText();

        } else if (id == R.id.nav_send) {
            showDummyText();

        } else if (id == R.id.nav_developer) {
            startActivity(new Intent(this, ColorActivity.class));

        } else if (id == R.id.nav_copyright) {

        } else if (id == R.id.nav_about) {
            showDummyText();

        } else if (id == R.id.nav_legal) {
            showDummyText();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //dummy methods
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

            case R.id.action_search:
                searchView.openSearch();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //search operation
    private void setAllSearchOption() {

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Uri uri = MainWordDBContract.Entry.buildUriWithWord(query.toUpperCase());
                Cursor cursor = getContentResolver().query(uri,
                        ConstantUtils.projectionOnlyWord, null, null, null);

                if (cursor != null && cursor.getCount() > 0) {
                    Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                    intent.setData(uri);
                    startActivity(intent);
                    searchView.closeSearch();
                    searchView.setCloseOnTintClick(false);
                }

                if (cursor != null) {
                    cursor.close();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.length() > 0) {
                    // TODO: 7/6/2017 add settings for user choice that user want search

                    String selection = MainWordDBContract.Entry.COLUMN_WORD + " like ? ";
                    //if you are try to search from any position of word
                    //then use
                    //String[] selectionArg = new String[]{"%"+newText+"%"};
                    //if you try to search from start of word the use this line
                    String[] selectionArg = new String[]{newText + "%"};

                    Cursor cursor = getContentResolver().query(MainWordDBContract.Entry.CONTENT_URI,
                            ConstantUtils.projectionOnlyWord, selection, selectionArg, null);

                    if (cursor != null && cursor.getCount() > 0) {
                        mAdapter.swapCursor(cursor);
                    }

                    return true;
                } else {
                    return false;
                }
            }
        });

        searchView.setSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewOpened() {

                if (fab.isShown()) {
                    fab.hide();
                }

            }

            @Override
            public void onSearchViewClosed() {
                if (!fab.isShown()) {
                    fab.show();
                }
            }
        });


//        searchView.setTintAlpha(200);
        searchView.adjustTintAlpha(0.8f);


        searchView.setOnVoiceClickedListener(this::askSpeechInput);

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
            startActivityForResult(intent, MaterialSearchView.REQUEST_VOICE);
        } catch (ActivityNotFoundException a) {
            a.printStackTrace();
            Utility.showLogThrowable("Activity not found", a);
            Toast.makeText(this, "Sorry Speech To Text is not " +
                    "supported in your device", Toast.LENGTH_SHORT).show();
        }
    }

    // Receiving speech input
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {

                    //Todo more accurate on settings
                    searchView.setQuery(searchWrd, false);
                    Uri uri = MainWordDBContract.Entry.
                            buildUriWithWord(searchWrd.toUpperCase());

                    Cursor cursor = getContentResolver().query(uri,
                            ConstantUtils.projectionOnlyWord, null, null, null);

                    if (cursor != null && cursor.getCount() > 0) {
                        Intent intent = new Intent(MainActivity.this,
                                DetailsActivity.class);
                        intent.setData(uri);
                        startActivity(intent);
                        searchView.closeSearch();
                        searchView.setCloseOnTintClick(false);
                    }

                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }

        } else if (requestCode == THEME_CHANGER_REQUEST_CODE && resultCode == RESULT_OK) {
            recreate();
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    //for loader
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, MainWordDBContract.Entry.CONTENT_URI,
                ConstantUtils.projectionOnlyWord, null, null,
                MainWordDBContract.Entry.COLUMN_WORD);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        // Update the data that the adapter uses to create ViewHolders
        Utility.showLog("Cursor data: " + data.getCount());
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
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
        searchView.closeSearch();
    }
}
