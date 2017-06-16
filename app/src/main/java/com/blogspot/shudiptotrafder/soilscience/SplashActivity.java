package com.blogspot.shudiptotrafder.soilscience;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.blogspot.shudiptotrafder.soilscience.data.MainWordDBContract;
import com.blogspot.shudiptotrafder.soilscience.utilities.ConstantUtills;
import com.blogspot.shudiptotrafder.soilscience.utilities.Utility;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Utility.setNightMode(this);

        //fixme add a progress dialog

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //SharedPreferences for database initializing state
        // for first time value

        SharedPreferences preferences = getSharedPreferences(
                ConstantUtills.DATABASE_INIT_SP_KEY, MODE_PRIVATE);
        //sate of database is initialized or not
        boolean state = preferences.getBoolean(
                ConstantUtills.DATABASE_INIT_SP_KEY, false);

        if (!state) {
            Utility.initializedDatabase(this);
        }

        final Thread checkForData = new Thread(){
            @Override
            public void run() {
                super.run();
                try{

                    Cursor cursor = getContentResolver().query(
                            MainWordDBContract.Entry.CONTENT_URI,
                            new String[]{MainWordDBContract.Entry._ID},
                            null,null,null);

                    if (cursor == null || cursor.getCount() == 0){
                        Utility.initializedDatabase(SplashActivity.this);
                    }

                    if (cursor != null) {
                        cursor.close();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    startActivity(new Intent(SplashActivity.this,MainActivity.class));
                    finish();
                }
            }
        };

        checkForData.start();
    }

}
