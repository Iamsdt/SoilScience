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
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.blogspot.shudiptotrafder.soilscience.adapter.ItemClickListener;
import com.blogspot.shudiptotrafder.soilscience.adapter.SearchAdapter;
import com.blogspot.shudiptotrafder.soilscience.data.MainWordDBContract;
import com.blogspot.shudiptotrafder.soilscience.data.MySuggestionProvider;
import com.blogspot.shudiptotrafder.soilscience.theme.ThemeUtils;
import com.blogspot.shudiptotrafder.soilscience.utilities.ConstantUtils;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity implements
        ItemClickListener {

    private SearchView searchView;
    private SearchRecentSuggestions suggestions;

    private ArrayList<String> arrayList;

    private SearchAdapter searchAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ThemeUtils.initialize(this);

        setContentView(R.layout.activity_search);

        Toolbar toolbar = findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);

        //ImageButton imageButton = findViewById(R.id.voice);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = findViewById(R.id.searchViewN);

        //assign suggestion
        suggestions = new SearchRecentSuggestions(this,
                MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);


        // Assumes current activity is the searchable activity
        if (searchManager != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }

        // Do not iconify the widget; expand it by default
        searchView.setIconifiedByDefault(false);


        searchView.setQueryRefinementEnabled(true);

        //fillArray(searchManager);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                validWordSubmit(query);
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

                    if (cursor != null) {
                        cursor.close();
                    }

                    return true;
                } else {
                    return false;
                }
            }
        });

        // must use this to prevent recreating activity
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return true;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                CursorAdapter selectedView = searchView.getSuggestionsAdapter();
                Cursor cursor = (Cursor) selectedView.getItem(position);
                int index = cursor.getColumnIndexOrThrow(SearchManager.SUGGEST_COLUMN_TEXT_1);
                searchView.setQuery(cursor.getString(index), true);
                return true;
            }
        });


        RecyclerView recyclerView = findViewById(R.id.searchViewRec);

        arrayList = new ArrayList<>();

        LinearLayoutManager manager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(manager);

        searchAdapter = new SearchAdapter(this, this, arrayList);

        recyclerView.setAdapter(searchAdapter);

        searchView.requestFocus(1);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    //to catch voice action data
    @Override
    protected void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            validWordSubmit(query);
        }
    }

    //check word validity
    private void validWordSubmit(String word) {
        Uri uri = MainWordDBContract.Entry.buildUriWithWord(word.toUpperCase());
        Cursor cursor = getContentResolver().query(uri,
                ConstantUtils.projectionOnlyWord, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            setRecentQuery(word);
            Intent intent = new Intent(SearchActivity.this, DetailsActivity.class);
            intent.setData(uri);
            startActivity(intent);

            searchView.clearFocus();
            clearSearchView();
        } else {
            //if word is not found in database
            //then show the data on searchView
            searchView.setQuery(word,false);
        }

        if (cursor != null) {
            cursor.close();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_historyClear:
                suggestions.clearHistory();
                break;

            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearSearchView();
        arrayList.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        searchAdapter.swapCursor(arrayList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_menu, menu);

        return true;
    }


    @Override
    public void onItemClickListener(String s) {
        Uri uri = MainWordDBContract.Entry.buildUriWithWord(s.toUpperCase());
        startActivity(new Intent(SearchActivity.this,
                DetailsActivity.class).setData(uri));

        setRecentQuery(s);

        searchView.clearFocus();

        //clear search view
        clearSearchView();
    }

    @Override
    public void onItemClickListener(int i) {
        //nothing to do
    }

    private void setRecentQuery(String query) {
        if (suggestions != null) {
            suggestions.saveRecentQuery(query, null);
        }
    }

    private void clearSearchView() {
        searchView.setQuery("", true);
    }
}
