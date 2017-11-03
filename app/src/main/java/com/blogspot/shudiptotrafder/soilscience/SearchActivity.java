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

import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.blogspot.shudiptotrafder.soilscience.adapter.SearchAdapter;
import com.blogspot.shudiptotrafder.soilscience.data.MainWordDBContract;
import com.blogspot.shudiptotrafder.soilscience.utilities.ConstantUtils;
import com.blogspot.shudiptotrafder.soilscience.utilities.Utility;

import java.util.ArrayList;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity implements SearchAdapter.ClickListener {

    private SearchView searchView;

    private final int REQUEST_VOICE = 126;

    private ArrayList<String> arrayList;

    private SearchAdapter searchAdapter;

    //to support vector drawables for lower api
    static {
        //complete add vector drawable support
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar =  findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);

        //ImageButton imageButton = findViewById(R.id.voice);

        RecyclerView recyclerView = findViewById(R.id.searchViewRec);

        arrayList = new ArrayList<>();

        LinearLayoutManager manager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(manager);

        searchAdapter = new SearchAdapter(this,this,arrayList);

        recyclerView.setAdapter(searchAdapter);

        //imageButton.setOnClickListener(v -> askSpeechInput());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void askSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speak your desire word");
        try {
            startActivityForResult(intent, REQUEST_VOICE);
        } catch (ActivityNotFoundException a) {
            a.printStackTrace();
            Utility.showLogThrowable("Activity not found", a);
            Toast.makeText(this, "Sorry Speech To Text is not " +
                    "supported in your device", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        if (searchManager != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
        searchView.setIconifiedByDefault(false);// Do not iconify the widget; expand it by default

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Uri uri = MainWordDBContract.Entry.buildUriWithWord(query.toUpperCase());
                Cursor cursor = getContentResolver().query(uri,
                        ConstantUtils.projectionOnlyWord, null, null, null);

                if (cursor != null && cursor.getCount() > 0) {
                    Intent intent = new Intent(SearchActivity.this, DetailsActivity.class);
                    intent.setData(uri);
                    startActivity(intent);
                }

                if (cursor != null) {
                    cursor.close();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 0) {

                    String selection = MainWordDBContract.Entry.COLUMN_WORD + " like ? ";
                    //if you are try to search from any position of word
                    //then use
                    //String[] selectionArg = new String[]{"%"+newText+"%"};
                    //if you try to search from start of word the use this line
                    String[] selectionArg = new String[]{newText + "%"};

                    Cursor cursor = getContentResolver().query(MainWordDBContract.Entry.CONTENT_URI,
                            ConstantUtils.projectionOnlyWord, selection, selectionArg, null);

                    if (cursor != null && cursor.getCount() > 0) {

                        cursor.moveToFirst();

                        arrayList.clear();

                        do {
                            String s = cursor.getString(0);
                            arrayList.add(s);

                        } while (cursor.moveToNext());

                        searchAdapter.swapCursor(arrayList);
                    }

                    if (cursor != null){
                        cursor.close();
                    }

                    return true;
                } else {
                    return false;
                }
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchView.setQuery(searchWrd, true);
                }
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStop() {
        super.onStop();
        arrayList.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        searchAdapter.swapCursor(arrayList);
    }

    @Override
    public void onItemClickListener(String s) {
        if (searchView != null) {
            searchView.setQuery(s, true);
        }
    }
}
