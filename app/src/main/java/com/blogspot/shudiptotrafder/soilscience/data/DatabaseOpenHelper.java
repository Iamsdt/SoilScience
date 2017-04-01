package com.blogspot.shudiptotrafder.soilscience.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * SoilScience
 * com.blogspot.shudiptotrafder.soilscience.data
 * Created by Shudipto Trafder on 4/1/2017.
 */

public class DatabaseOpenHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "soilscience.db";
    private static final int DB_VERSION = 1;


    public DatabaseOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE_SQL = "CREATE TABLE " +
                MainWordDBContract.MainWordDBEntry.TABLE_NAME +" ( "+
                MainWordDBContract.MainWordDBEntry._ID + " INTEGER PRIMARY KEY, "
                + MainWordDBContract.MainWordDBEntry.COLUMN_WORD + " TEXT NOT NULL, "
                + MainWordDBContract.MainWordDBEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL," +
                " UNIQUE ( " + MainWordDBContract.MainWordDBEntry.COLUMN_WORD + ") ON CONFLICT REPLACE);";

        Log.e("Sql",CREATE_TABLE_SQL);

        db.execSQL(CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MainWordDBContract.MainWordDBEntry.TABLE_NAME);
        onCreate(db);
    }

}
