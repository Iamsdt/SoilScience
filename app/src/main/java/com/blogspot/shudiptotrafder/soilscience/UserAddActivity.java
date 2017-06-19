package com.blogspot.shudiptotrafder.soilscience;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.blogspot.shudiptotrafder.soilscience.data.MainWordDBContract;
import com.ftinc.scoop.Scoop;

public class UserAddActivity extends AppCompatActivity {

    //Edit text
    private EditText wordEt, desEt;
    //TextInputLayout
    private TextInputLayout wordLayout,desLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Scoop.getInstance().apply(this);

        setContentView(R.layout.activity_user_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        wordEt = (EditText) findViewById(R.id.addWordEt);
        desEt = (EditText) findViewById(R.id.addDesEt);

        wordLayout = (TextInputLayout) findViewById(R.id.addWordLayout);
        desLayout = (TextInputLayout) findViewById(R.id.addDesLayout);

        Button button = (Button) findViewById(R.id.submitBtn);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWord();
            }
        });


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    private void addWord() {

        if (!validateWord()){
            return;
        }

        if (!validateDes()){
            return;
        }

        String word = wordEt.getText().toString().trim();
        String des = desEt.getText().toString().trim();

        ContentValues values = new ContentValues();
        values.put(MainWordDBContract.Entry.COLUMN_WORD,word);
        values.put(MainWordDBContract.Entry.COLUMN_DESCRIPTION,des);
        values.put(MainWordDBContract.Entry.COLUMN_FAVOURITE,false);
        values.put(MainWordDBContract.Entry.COLUMN_USER,true);

        Uri uri = getContentResolver().insert(MainWordDBContract.Entry.CONTENT_URI,values);

        if (uri != null){
            finish();
        }
    }

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

    private void requestFocus(View view) {
        if (view.requestFocus()){
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

}
