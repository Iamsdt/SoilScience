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

/**
 * SoilScience
 * com.blogspot.shudiptotrafder.soilscience.data
 * Created by Shudipto Trafder on 4/1/2017.
 */

public class DataProvider  extends ContentProvider {

    //use to get all data from this path
    public static final int TASKS = 100;
    //use to get single data from a single row
    public static final int TASK_WITH_ID = 101;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    DatabaseOpenHelper openHelper;

    public static UriMatcher buildUriMatcher() {

        // Initialize a UriMatcher with no matches by passing in NO_MATCH to the constructor
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        /*
           All paths added to the UriMatcher have a corresponding int.
           For each kind of uri you may want to access, add the corresponding match with addURI.
           The two calls below add matches for the task directory and a single item by ID.
         */
        uriMatcher.addURI(MainWordDBContract.AUTHORITY, MainWordDBContract.PATH_TASKS, TASKS);
        uriMatcher.addURI(MainWordDBContract.AUTHORITY, MainWordDBContract.PATH_TASKS + "/*", TASK_WITH_ID);

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

        final SQLiteDatabase db = openHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);

        Cursor returnCursor;

        switch (match){

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

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final SQLiteDatabase db = openHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        //return uri
        Uri returnUri;

        switch (match){
            case TASKS:
                long id = db.insert(TABLE_NAME,null,values);
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
        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

}
