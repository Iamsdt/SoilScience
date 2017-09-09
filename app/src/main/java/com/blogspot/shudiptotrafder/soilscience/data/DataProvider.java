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

package com.blogspot.shudiptotrafder.soilscience.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.blogspot.shudiptotrafder.soilscience.data.MainWordDBContract.Entry.TABLE_NAME;
import static com.blogspot.shudiptotrafder.soilscience.data.MainWordDBContract.PATH_TASKS;

/**
 * SoilScience
 * com.blogspot.shudiptotrafder.soilscience.data
 * Created by Shudipto Trafder on 4/1/2017.
 */

public class DataProvider extends ContentProvider {

    /**
     * class for access database
     * fill database and query data
     */

    //use to get all data from this path
    private static final int TASKS = 100;
    //use to get single data from a single row
    private static final int TASK_WITH_ID = 101;
    //where uri match
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    //database helper
    private DatabaseOpenHelper openHelper;

    /**
     * that create a uri
     * called before CURID function (ex: insert,delete,update,query)
     *
     * @return a uri matcher that's help's to detect uri match
     * with full table or single data of table row
     */
    private static UriMatcher buildUriMatcher() {

        // Initialize a UriMatcher with no matches by passing in NO_MATCH to the constructor
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        /*
           All paths added to the UriMatcher have a corresponding int.
           For each kind of uri you may want to access, add the corresponding match with addURI.
           The two calls below add matches for the task directory and a single item by ID.
         */
        uriMatcher.addURI(MainWordDBContract.AUTHORITY, PATH_TASKS, TASKS);
        /*for data in a row
        here we access data with word column that's is a string
        so here path/*
        if we access id that is a number then we use
        path/# instead of path/*
        */
        uriMatcher.addURI(MainWordDBContract.AUTHORITY, PATH_TASKS + "/*", TASK_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        openHelper = new DatabaseOpenHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        //get database from Database open helper
        final SQLiteDatabase db = openHelper.getReadableDatabase();

        //first check  database is match with what type
        int match = sUriMatcher.match(uri);

        Cursor returnCursor;

        switch (match) {

            case TASKS:
                returnCursor = db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            //one row
            case TASK_WITH_ID:
                // Get the id from the URI
                String word = uri.getPathSegments().get(1);
                // Selection is the word column = ?, and the Selection args = the word from the URI
                String mSelection = MainWordDBContract.Entry.COLUMN_WORD + " = ?";
                String[] mSelectionArg = new String[]{word};

                // Construct a query as you would normally, passing in the selection/args
                returnCursor = db.query(TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArg,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: query " + uri);
        }

        return returnCursor;
    }

    //we are not exported our database
    //for practice purpose we fill this methods

    /**
     * This methods used for exported database
     * handles requests for the MIME type of data
     * it gives a way to standardize the data formats
     * that your provider accesses, and this can be useful for data organization.
     *
     * @param uri that's check database
     * @return MIME type
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        int match = sUriMatcher.match(uri);

        switch (match) {
            case TASKS:
                // directory
                return "vnd.android.cursor.dir" + "/" + MainWordDBContract.AUTHORITY + "/" + MainWordDBContract.PATH_TASKS;
            case TASK_WITH_ID:
                // single item type
                return "vnd.android.cursor.item" + "/" + MainWordDBContract.AUTHORITY + "/" + MainWordDBContract.PATH_TASKS;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final SQLiteDatabase db = openHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        //return uri
        Uri returnUri;

        switch (match) {
            //only one case because
            //we insert data at once
            case TASKS:
                long id = db.insert(TABLE_NAME, null, values);
                if (id > 0) {
                    //success
                    returnUri = ContentUris.withAppendedId(MainWordDBContract.Entry.CONTENT_URI, id);
                } else {
                    throw new SQLException("Failed to Insert data" + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        //notify data has changed
        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return returnUri;
    }

    //we don't allow user to delete data
    //for education purpose we fill this methods
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        //get database from Database open helper
        final SQLiteDatabase db = openHelper.getReadableDatabase();

        //first check  database is match with what type
        int match = sUriMatcher.match(uri);

        int deleteNumber;

        switch (match) {
            //we delete data one by one
            case TASK_WITH_ID:

                // Get the id from the URI
                String word = uri.getPathSegments().get(1);

                String mSelection = MainWordDBContract.Entry.COLUMN_WORD + " = ?";
                String[] mSelectionArg = new String[]{word};

                deleteNumber = db.delete(MainWordDBContract.Entry.TABLE_NAME, mSelection, mSelectionArg);

                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: data not delete " + uri);

        }

        //notify data has changed
        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return deleteNumber;
    }

    //we just modify favourite data
    //for education purpose we fill this methods
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        //get database from Database open helper
        final SQLiteDatabase db = openHelper.getReadableDatabase();

        //first check  database is match with what type
        int match = sUriMatcher.match(uri);

        int updateNumber;

        switch (match) {
            //we delete data one by one
            case TASK_WITH_ID:

                // Get the id from the URI
                String word = uri.getPathSegments().get(1);
                String mSelection = MainWordDBContract.Entry.COLUMN_WORD + " = ?";
                String[] mSelectionArg = new String[]{word};

                updateNumber = db.update(MainWordDBContract.Entry.TABLE_NAME, values, mSelection, mSelectionArg);

                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: data not update " + uri);

        }

        //notify data has changed
        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return updateNumber;
    }

}
