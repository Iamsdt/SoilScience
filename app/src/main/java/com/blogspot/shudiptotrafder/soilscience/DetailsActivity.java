package com.blogspot.shudiptotrafder.soilscience;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Slide;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.shudiptotrafder.soilscience.data.MainWordDBContract;
import com.blogspot.shudiptotrafder.soilscience.settings.SettingsActivity;
import com.blogspot.shudiptotrafder.soilscience.theme.ThemeUtils;
import com.blogspot.shudiptotrafder.soilscience.utilities.ConstantUtils;
import com.blogspot.shudiptotrafder.soilscience.utilities.Utility;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Locale;

public class DetailsActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        TextToSpeech.OnInitListener {


    //for select data column from database
    private static final String[] projection = {
            MainWordDBContract.Entry.COLUMN_WORD,
            MainWordDBContract.Entry.COLUMN_DESCRIPTION
    };

    //id or position for return array
    private static final int WORD_ID = 0;
    private static final int DESCRIPTION_ID = 1;

    //uri from previous activity
    private Uri mUri = null;

    //view
    private TextView wordTV;
    private TextView descriptionTV;

    private TextToSpeech toSpeech;

    //selected word from Main activity
    String wordForTTS = null;
    private String descriptionOfWord = null;


    //vector support
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ThemeUtils.initialize(this);

        setContentView(R.layout.activity_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //assign view
        NestedScrollView scrollView = (NestedScrollView) findViewById(R.id.detailsScrollView);

        wordTV = (TextView) findViewById(R.id.details_word);
        descriptionTV = (TextView) findViewById(R.id.details_description);

        toSpeech = new TextToSpeech(this,this);

        setupWindowAnimations();

        //set uri
        try {
            assert getIntent().getData() != null;
            mUri = getIntent().getData().normalizeScheme();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Uri can not null");
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.details_fab);
        fab.setOnClickListener(view -> setTTS(wordForTTS));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //initialize loader
        getSupportLoaderManager().initLoader(ConstantUtils.DETAILS_LOADER_ID, null, this);

        scrollView.setSmoothScrollingEnabled(true);
        scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {

            if (scrollY > 1){
                fab.hide();
            } else if (scrollY < 1){
                fab.show();
            }

        });

    }

    //enter
    private void setupWindowAnimations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Fade fade = new Fade();
            fade.setDuration(1000);
            getWindow().setEnterTransition(fade);

            Slide slide = new Slide();
            slide.setDuration(1000);
            getWindow().setReturnTransition(slide);
        }

    }


    private void setTTS(String selectedWord) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toSpeech.speak(selectedWord,TextToSpeech.QUEUE_FLUSH,null,null);

        } else {
            toSpeech.speak(selectedWord,TextToSpeech.QUEUE_FLUSH,null);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (toSpeech != null) {
            toSpeech.stop();
            toSpeech.shutdown();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(ConstantUtils.DETAILS_LOADER_ID, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details,menu);
        MenuItem shareItem = menu.findItem(R.id.action_share);
        ShareActionProvider myShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);

        myShareActionProvider.setShareIntent(createShareIntent());

        return true;
    }

    // Create and return the Share Intent
    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        if (wordForTTS != null && descriptionOfWord != null){
            shareIntent.putExtra(Intent.EXTRA_TEXT, wordForTTS+":"+descriptionOfWord);
        }

        return shareIntent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home){
            onBackPressed();

        } else if (id == R.id.action_settings){
            startActivity(new Intent(this, SettingsActivity.class));

        } else if (id == R.id.action_favourite){
            ContentValues values = new ContentValues();
            values.put(MainWordDBContract.Entry.COLUMN_FAVOURITE,true);
            int update = getContentResolver().update(mUri,values,null,null);

            if (update != -1){
                Toast.makeText(this, "Add to favourite", Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {
            case ConstantUtils.DETAILS_LOADER_ID:
                return new CursorLoader(this, mUri,
                        //number of column select
                        projection,
                        //we don't need selection agr
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

        wordForTTS = word;
        descriptionOfWord = description;

        Bundle bundle = new Bundle();
        bundle.putString("Details_Word_",word);
        FirebaseAnalytics.getInstance(this).logEvent("Word_Details",bundle);

        wordTV.setText(word);
        descriptionTV.setText(description);

        //set Size
        descriptionTV.setTextSize(getDesTextSize());
        wordTV.setTextSize(Utility.getTextSize(this));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //nothing to do
    }

    @Override
    public void onInit(int status) {

        if (status != TextToSpeech.ERROR){

            int result = toSpeech.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED||
                    result == TextToSpeech.ERROR_NOT_INSTALLED_YET){

                Intent installIntent = new Intent();
                installIntent.setAction(
                        TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);

            }

        }
    }

    //set text size
    private int getDesTextSize(){

        int size = Utility.getTextSize(this);

        if (size >= 20){
            size = size - 3;

        } else if (size == 17){
            size = size - 2;

        } else {
            size = size - 1;
        }
        return size;

    }
}
