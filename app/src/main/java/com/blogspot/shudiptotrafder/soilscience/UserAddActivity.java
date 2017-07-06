package com.blogspot.shudiptotrafder.soilscience;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.blogspot.shudiptotrafder.soilscience.data.MainWordDBContract;
import com.blogspot.shudiptotrafder.soilscience.data.RealTimeDataStructure;
import com.blogspot.shudiptotrafder.soilscience.theme.ThemeUtils;
import com.blogspot.shudiptotrafder.soilscience.utilities.Utility;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserAddActivity extends AppCompatActivity {

    //Edit text
    private EditText wordEt, desEt;
    //TextInputLayout
    private TextInputLayout wordLayout, desLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utility.setNightMode(this);
        ThemeUtils.initialize(this);

        setContentView(R.layout.activity_user_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // views
        wordEt = (EditText) findViewById(R.id.addWordEt);
        desEt = (EditText) findViewById(R.id.addDesEt);

        wordLayout = (TextInputLayout) findViewById(R.id.addWordLayout);
        desLayout = (TextInputLayout) findViewById(R.id.addDesLayout);

        Button button = (Button) findViewById(R.id.submitBtn);


        //button click listener
        button.setOnClickListener(v -> addWord());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    /**
     * This method will add user word into offline and real time data base
     * before add we need to check word is validate
     */
    private void addWord() {

        //validation rule for word
        if (!validateWord()) {
            return;
        }

        //validation rule for description
        if (!validateDes()) {
            return;
        }

        //get text form edit text
        String word = wordEt.getText().toString().trim();
        String des = desEt.getText().toString().trim();

        //put those value into offline data base
        ContentValues values = new ContentValues();
        values.put(MainWordDBContract.Entry.COLUMN_WORD, word);
        values.put(MainWordDBContract.Entry.COLUMN_DESCRIPTION, des);
        values.put(MainWordDBContract.Entry.COLUMN_FAVOURITE, false);
        values.put(MainWordDBContract.Entry.COLUMN_USER, true);

        final boolean[] uploadWordStatus = {false};

        //todo also add word to real time database
        //if you don't use push then you data will be replaced
        if (Utility.isUploadEnabled(this)){

            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference mDatabaseReference = firebaseDatabase.getReference().child("User");

            RealTimeDataStructure dataStructure = new RealTimeDataStructure(word, des);
            mDatabaseReference.child(Build.MODEL).push()
                    .setValue(dataStructure, (databaseError, databaseReference) -> {
                        if (databaseError != null){
                            //failed
                            uploadWordStatus[0] = false;
                        } else {
                            //success full
                            uploadWordStatus[0] = true;
                        }
                    });
        } else {
            //if user don't want to upload his word
            //this will consider as uploaded
            uploadWordStatus[0] = true;
        }

        values.put(MainWordDBContract.Entry.COLUMN_UPLOAD, uploadWordStatus[0]);

        Uri uri = getContentResolver().insert(MainWordDBContract.Entry.CONTENT_URI, values);

        //if uri is not null -> data inserted successfully
        // if operation successfull the leave this activity
        if (uri != null) {
            finish();
        }
    }

    /**
     * This method for validate word
     * if word length is lower than 1
     * then it will be show an error message
     */

    private boolean validateWord() {

        String word = wordEt.getText().toString().trim();

        if (word.isEmpty() && word.length() < 1) {
            wordLayout.setError("Please add a valid word");
            requestFocus(wordEt);
            return false;
        } else {
            wordLayout.setErrorEnabled(false);
            return true;
        }

    }

    /**
     * This method for validate description
     * if word length is lower than 5
     * then it will be show an error message
     */
    private boolean validateDes() {

        String des = desEt.getText().toString().trim();

        if (des.isEmpty() && des.length() < 5) {
            desLayout.setError("please add real description");
            requestFocus(desEt);
            return false;
        } else {
            desLayout.setErrorEnabled(false);
            return true;
        }

    }

    /**
     * This methods for request focus on edit text
     * if user input is not valid
     * then it shown
     *
     * @param view this vies will be focused
     */
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

}
