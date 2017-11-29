/*
 * Copyright {2017} {Shudipto Trafder}
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blogspot.shudiptotrafder.soilscience;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.shudiptotrafder.soilscience.adapter.CustomCursorAdapter;
import com.blogspot.shudiptotrafder.soilscience.adapter.ItemClickListener;
import com.blogspot.shudiptotrafder.soilscience.data.DatabaseUtils;
import com.blogspot.shudiptotrafder.soilscience.data.MainWordDBContract;
import com.blogspot.shudiptotrafder.soilscience.services.DataService;
import com.blogspot.shudiptotrafder.soilscience.services.UploadServices;
import com.blogspot.shudiptotrafder.soilscience.settings.SettingsActivity;
import com.blogspot.shudiptotrafder.soilscience.theme.ColorActivity;
import com.blogspot.shudiptotrafder.soilscience.theme.ThemeUtils;
import com.blogspot.shudiptotrafder.soilscience.utilities.ConstantUtils;
import com.blogspot.shudiptotrafder.soilscience.utilities.FileImportExportUtils;
import com.blogspot.shudiptotrafder.soilscience.utilities.Utility;
import com.github.fabtransitionactivity.SheetLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int THEME_CHANGER_REQUEST_CODE = 111;

    //recycler view adapter
    private CustomCursorAdapter mAdapter;

    //floating action button
    private FloatingActionButton fab;
    private SheetLayout sheetLayout;

    private boolean nightModeStatus;

    //to support vector drawables for lower api
    static {
        //complete add vector drawable support
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ThemeUtils.initialize(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //assign view
        //searchView = findViewById(R.id.search_view);

        RecyclerView recyclerView = findViewById(R.id.mainRecycleView);
        final LinearLayoutManager manager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);

        mAdapter = new CustomCursorAdapter(this, this);
        recyclerView.setAdapter(mAdapter);

        //load app intro
        SharedPreferences preferences = getSharedPreferences(ConstantUtils.APP_OPEN_FIRST_TIME, MODE_PRIVATE);

        boolean status = preferences.getBoolean(ConstantUtils.APP_INTRO_STATUS, false);

        if (!status) {
            startActivity(new Intent(this, MyAppIntro.class));
        }

        SharedPreferences sharedPreferences =
                getSharedPreferences(ConstantUtils.NIGHT_MODE_SP_KEY, MODE_PRIVATE);
        nightModeStatus = sharedPreferences.getBoolean(ConstantUtils.NIGHT_MODE_VALUE_KEY, false);


        //set all search action
        //setAllSearchOption();

        /*
         Ensure a loader is initialized and active. If the loader doesn't already exist, one is
         created, otherwise the last created loader is re-used.
         */
        getSupportLoaderManager().initLoader(ConstantUtils.MAIN_LOADER_ID, null, this);


        fab = findViewById(R.id.main_fab);
        sheetLayout = findViewById(R.id.bottom_sheet);
        sheetLayout.setFab(fab);

        fab.setOnClickListener(view -> {
            sheetLayout.expandFab();
            showRandomised();
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


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        //deprecated
        //drawer.setDrawerListener(toggle);
        //fix in this way
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FirebaseAnalytics.getInstance(this)
                .logEvent(FirebaseAnalytics.Event.APP_OPEN, null);

    }

    private void showRandomised() {

        String word = mAdapter.getRandomWord();
        Uri mUri = MainWordDBContract.Entry.buildUriWithWord(word);
        String description = Utility.getWordWithDes(getBaseContext(), mUri);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint("InflateParams") final View dialogView = inflater.inflate(R.layout.random_layout, null);
        dialogBuilder.setView(dialogView);

        final TextView wordTV = dialogView.findViewById(R.id.rand_word);
        final TextView desTV = dialogView.findViewById(R.id.rand_description);

        final ImageView backImg = dialogView.findViewById(R.id.rand_img_back);
        final ImageView favImg = dialogView.findViewById(R.id.rand_fav_img);


        wordTV.setText(word);
        desTV.setText(description);

        AlertDialog b = dialogBuilder.create();
        //b.setCancelable(false);
        b.show();

        //set touch outside off
        b.setCanceledOnTouchOutside(false);

        b.setOnDismissListener(dialog -> {
            if (sheetLayout.isFabExpanded()) {
                sheetLayout.contractFab();
            }
        });

        //control dialog size
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

        if (b.getWindow() != null) {
            lp.copyFrom(b.getWindow().getAttributes());
            lp.width = displayMetrics.widthPixels - 5;
            lp.height = displayMetrics.heightPixels - 50;
            b.getWindow().setAttributes(lp);
        }

        backImg.setOnClickListener(view1 -> {
            if (b.isShowing()) {
                b.dismiss();
                sheetLayout.contractFab();
            }
        });

        favImg.setOnClickListener(view1 -> {
            ContentValues values = new ContentValues();
            values.put(MainWordDBContract.Entry.COLUMN_FAVOURITE, true);
            int update = getContentResolver().update(mUri, values, null, null);

            if (update != -1) {
                Toast.makeText(this, "Add to favourite", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        //check network ability
        //if network is not found don't start services
        if (Utility.isNetworkAvailable(this)) {

            FirebaseAuth auth = FirebaseAuth.getInstance();

            //first check remote config status and is any word to left for upload
            //then sign in anomalously
            // if successful run those services

            //prevent double check
            boolean remoteConfigStatus = DatabaseUtils.getRemoteConfigStatus(this);
            boolean uploadLeftStatus = DatabaseUtils.checkUploadLeft(this);

            if (remoteConfigStatus || uploadLeftStatus) {
                //first sign in the retrieve value
                auth.signInAnonymously().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        //check remote config
                        if (remoteConfigStatus) {
                            Intent intent = new Intent(getApplicationContext(),
                                    DataService.class);
                            startService(intent);
                            //prevent start again and again
                        }

                        //check if any thing left to upload
                        if (uploadLeftStatus) {
                            startService(new Intent(getApplicationContext(),
                                    UploadServices.class));
                        }
                    }
                });
            }

            //if nothing to upload and service is running then stop the services
            if (!uploadLeftStatus) {
                if (UploadServices.UploadServiceRunning) {
                    Intent intent = new Intent(this, UploadServices.class);
                    stopService(intent);
                }
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (sheetLayout.isFabExpanded()) {
            sheetLayout.contractFab();
        }

        // re-queries for all tasks
        getSupportLoaderManager().restartLoader(ConstantUtils.MAIN_LOADER_ID, null, this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else if (sheetLayout.isFabExpanded()) {
            sheetLayout.contractFab();

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

        } else if (id == R.id.nav_favourite) {
            startActivity(new Intent(this, FavouriteActivity.class));

        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));

        } else if (id == R.id.nav_choose_theme) {
            startActivityForResult(ColorActivity
                    .createIntent(this), THEME_CHANGER_REQUEST_CODE);

        } else if (id == R.id.nav_add) {
            startActivity(new Intent(this, UserAddActivity.class));

        } else if (id == R.id.nav_developer) {
            startActivity(new Intent(this, DeveloperActivity.class));

        } else if (id == R.id.nav_copyright) {
            Copyright();

        } else if (id == R.id.nav_about) {
            startActivity(new Intent(this, AboutActivity.class));


        } else if (id == R.id.nav_legal) {
            termsOfUse();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //copyright dialog
    private void Copyright() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Copyright");
        builder.setMessage(getString(R.string.copyrightMS));
        builder.setNeutralButton("ok", (dialogInterface, i) -> {
        });
        builder.setIcon(getResources().getDrawable(R.drawable.ic_copyright));

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //terms of use dialog
    private void termsOfUse() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Terms of Use");
        builder.setMessage(getString(R.string.terms_of_use));
        builder.setNeutralButton("ok", (dialogInterface, i) -> {
        });
        builder.setIcon(getResources().getDrawable(R.drawable.ic_law));

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    //for option menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        MenuItem nightMode = menu.findItem(R.id.nightMode);

        if (nightModeStatus) {
            nightMode.setIcon(R.drawable.ic_half_moon);

        } else {
            nightMode.setIcon(R.drawable.ic_wb_sunny_black_24dp);

        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_search:
                //move search view to new activity
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
                return true;

            case R.id.nightMode:
                //complete change icon
                SharedPreferences sharedPreferences =
                        getSharedPreferences(ConstantUtils.NIGHT_MODE_SP_KEY, MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (nightModeStatus) {
                    editor.putBoolean(ConstantUtils.NIGHT_MODE_VALUE_KEY, false);
                    editor.apply();
                    recreate();

                } else {
                    editor.putBoolean(ConstantUtils.NIGHT_MODE_VALUE_KEY, true);
                    editor.apply();
                    recreate();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == THEME_CHANGER_REQUEST_CODE && resultCode == RESULT_OK) {
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

        SharedPreferences preferences = getSharedPreferences(ConstantUtils.APP_OPEN_FIRST_TIME,
                MODE_PRIVATE);

        boolean restoreState = preferences.getBoolean(ConstantUtils.USER_RESTORE, false);

        if (!restoreState) {
            new Thread(() -> {
                FileImportExportUtils.checkFileAvailable(getBaseContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(ConstantUtils.USER_RESTORE, true);
                editor.apply();
            });
        }

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
        //searchView.closeSearch();
    }

    /**
     * onClick listener for recycler view
     * called if click any item on recycler view
     *
     * @param i int number
     */
    @Override
    public void onItemClickListener(int i) {
        //nothing to do
    }
}