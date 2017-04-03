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

import static com.blogspot.shudiptotrafder.soilscience.data.MainWordDBContract.MainWordDBEntry.TABLE_NAME;
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
    public static final int TASKS = 100;
    //use to get single data from a single row
    public static final int TASK_WITH_ID = 101;
    //where uri match
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    //database helper
    DatabaseOpenHelper openHelper;

    /**
     * that create a uri
     * called before CURID function (ex: insert,delete,update,query)
     *
     * @return a uri matcher that's help's to detect uri match
     * with full table or single data of table row
     */
    public static UriMatcher buildUriMatcher() {

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
                // Selection is the _ID column = ?, and the Selection args = the row ID from the URI
                String mSelection = MainWordDBContract.MainWordDBEntry.COLUMN_WORD + " = ?";
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
    //for education purpose we fill this methods

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
                    returnUri = ContentUris.withAppendedId(MainWordDBContract.MainWordDBEntry.CONTENT_URI, id);
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
                // Selection is the _ID column = ?, and the Selection args = the row ID from the URI
                String mSelection = MainWordDBContract.MainWordDBEntry.COLUMN_WORD + " = ?";
                String[] mSelectionArg = new String[]{word};

                deleteNumber = db.delete(MainWordDBContract.MainWordDBEntry.TABLE_NAME, mSelection, mSelectionArg);

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

    //we don't allow user to update data
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
                // Selection is the _ID column = ?, and the Selection args = the row ID from the URI
                String mSelection = MainWordDBContract.MainWordDBEntry.COLUMN_WORD + " = ?";
                String[] mSelectionArg = new String[]{word};

                updateNumber = db.update(MainWordDBContract.MainWordDBEntry.TABLE_NAME, values, mSelection, mSelectionArg);

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
