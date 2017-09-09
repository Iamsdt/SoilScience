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

package com.blogspot.shudiptotrafder.soilscience.services;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.blogspot.shudiptotrafder.soilscience.data.MainWordDBContract;
import com.blogspot.shudiptotrafder.soilscience.data.RealTimeDataStructure;
import com.blogspot.shudiptotrafder.soilscience.utilities.Utility;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Shudipto Trafder.
 * on 7/6/2017
 */

public class UploadServices extends Service {


    // complete: 7/7/2017 same word add again and again

    public static boolean UploadServiceRunning = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        UploadServiceRunning = true;

        Cursor cursor = getContentResolver().query(
                MainWordDBContract.Entry.CONTENT_URI,
                //projection
                new String[]{MainWordDBContract.Entry.COLUMN_WORD,
                        MainWordDBContract.Entry.COLUMN_DESCRIPTION},
                //selection
                MainWordDBContract.Entry.COLUMN_UPLOAD + " = ? ",
                //selection Arg
                new String[]{"0"},
                null);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseReference = firebaseDatabase
                .getReference().child("User");

        ContentValues values = new ContentValues();
        final boolean[] status = {true};

        if (cursor != null) {

            cursor.moveToFirst();

            do {

                String word = cursor.getString(0);
                String des = cursor.getString(1);

                Uri uri = MainWordDBContract.Entry.buildUriWithWord(word);

                Utility.showLog("Upload left: "+word);

                Utility.setAnalyticsData(this,"Data Upload:",
                        "data upload to remote: "+word);

                RealTimeDataStructure dataStructure = new RealTimeDataStructure(word, des);

                mDatabaseReference.child(Build.MODEL).push()
                        .setValue(dataStructure, (databaseError, databaseReference) ->
                                status[0] = databaseError == null);

                values.put(MainWordDBContract.Entry.COLUMN_UPLOAD, status[0]);
                getContentResolver().update(uri,values,null,null);

            } while (cursor.moveToNext());

            cursor.close();
        }


        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

