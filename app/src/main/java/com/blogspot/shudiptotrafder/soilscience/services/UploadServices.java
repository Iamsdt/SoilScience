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
        super("Upload data");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        Cursor cursor = getContentResolver().query(
                MainWordDBContract.Entry.CONTENT_URI,
                //projection
                new String[]{MainWordDBContract.Entry.COLUMN_WORD,
                        MainWordDBContract.Entry.COLUMN_DESCRIPTION},
                //selection
                MainWordDBContract.Entry.COLUMN_UPLOAD + " =? ",
                //selection Arg
                new String[]{"0"},
                null);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseReference = firebaseDatabase.getReference().child("User");

        ContentValues values = new ContentValues();

        if (cursor != null) {

            cursor.moveToFirst();

            do {

                String word = cursor.getString(0);
                String des = cursor.getString(1);

                RealTimeDataStructure dataStructure = new RealTimeDataStructure(word, des);

                mDatabaseReference.child(Build.MODEL).push()
                        .setValue(dataStructure, (databaseError, databaseReference) -> {
                            if (databaseError != null) {
                                //failed
                                values.put(MainWordDBContract.Entry.COLUMN_UPLOAD, false);
                            } else {
                                //success full
                                values.put(MainWordDBContract.Entry.COLUMN_UPLOAD, true);
                            }
                        });

                Uri uri = MainWordDBContract.Entry.buildUriWithWord(word);

                getContentResolver().update(uri,values,null,null);

            } while (cursor.moveToNext());

            cursor.close();
        }
    }
}

