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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.blogspot.shudiptotrafder.soilscience.utilities.ConstantUtils;

/**
 * Created by Shudipto Trafder.
 * on 4/1/2017
 */

class DatabaseOpenHelper extends SQLiteOpenHelper {

     DatabaseOpenHelper(Context context) {
        super(context, ConstantUtils.DB_NAME, null, ConstantUtils.DB_VERSION);
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
                + MainWordDBContract.Entry.COLUMN_UPLOAD + " BOOLEAN, "
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
