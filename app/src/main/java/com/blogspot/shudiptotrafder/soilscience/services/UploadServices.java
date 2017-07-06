package com.blogspot.shudiptotrafder.soilscience.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;

import com.blogspot.shudiptotrafder.soilscience.data.MainWordDBContract;
import com.blogspot.shudiptotrafder.soilscience.data.RealTimeDataStructure;
import com.blogspot.shudiptotrafder.soilscience.utilities.Utility;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Shudipto on 7/6/2017.
 */

public class UploadServices extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public UploadServices() {
        super("Upload_data");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

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
        DatabaseReference mDatabaseReference = firebaseDatabase.getReference().child("User");

        ContentValues values = new ContentValues();
        final boolean[] status = {false};

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
    }
}

