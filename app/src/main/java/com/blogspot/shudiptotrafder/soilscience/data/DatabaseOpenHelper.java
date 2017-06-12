package com.blogspot.shudiptotrafder.soilscience.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.blogspot.shudiptotrafder.soilscience.utilities.ConstantUtills;

/**
 * SoilScience
 * com.blogspot.shudiptotrafder.soilscience.data
 * Created by Shudipto Trafder on 4/1/2017.
 */

class DatabaseOpenHelper extends SQLiteOpenHelper {

     DatabaseOpenHelper(Context context) {
        super(context, ConstantUtills.DB_NAME, null, ConstantUtills.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE_SQL = "CREATE TABLE " +
                MainWordDBContract.Entry.TABLE_NAME +" ( "+
                MainWordDBContract.Entry._ID + " INTEGER PRIMARY KEY, "
                + MainWordDBContract.Entry.COLUMN_WORD + " TEXT NOT NULL, "
                + MainWordDBContract.Entry.COLUMN_DESCRIPTION + " TEXT NOT NULL, "
                + MainWordDBContract.Entry.COLUMN_FAVOURITE + " BOOLEAN, "
                + MainWordDBContract.Entry.COLUMN_USER + " BOOLEAN, "
                + " UNIQUE ( " + MainWordDBContract.Entry.COLUMN_WORD + ") ON CONFLICT REPLACE);";

        //Log.e("Sql",CREATE_TABLE_SQL);

        db.execSQL(CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MainWordDBContract.Entry.TABLE_NAME);
        onCreate(db);
    }

}
