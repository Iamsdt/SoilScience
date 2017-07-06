package com.blogspot.shudiptotrafder.soilscience;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.blogspot.shudiptotrafder.soilscience.data.DatabaseUtils;
import com.blogspot.shudiptotrafder.soilscience.data.MainWordDBContract;
import com.blogspot.shudiptotrafder.soilscience.services.DataService;
import com.blogspot.shudiptotrafder.soilscience.services.UploadServices;
import com.blogspot.shudiptotrafder.soilscience.theme.ThemeUtils;
import com.blogspot.shudiptotrafder.soilscience.utilities.ConstantUtils;
import com.blogspot.shudiptotrafder.soilscience.utilities.Utility;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Utility.setNightMode(this);
        ThemeUtils.initialize(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle(getString(R.string.splash_pd_title));
        dialog.setMessage(getString(R.string.splash_pd_meg));
        dialog.setCancelable(false);

        //SharedPreferences for database initializing state
        // for first time value
        SharedPreferences preferences = getSharedPreferences(
                ConstantUtils.DATABASE_INIT_SP_KEY, MODE_PRIVATE);
        //sate of database is initialized or not
        boolean state = preferences.getBoolean(
                ConstantUtils.DATABASE_INIT_SP_KEY, false);

        if (!state) {

            dialog.show();

            DatabaseUtils.initializedDatabase(this);

            if (checkDataBaseStatus()){

                if (dialog.isShowing()){
                    dialog.dismiss();
                }
                runThread(100);//1s
            }
        } else {
            //check network ability
            //don't start services
            //it safe user battery
            if(Utility.isNetworkAvailable(this)){

                if (checkUploadLeft()){
                    startService(new Intent(this, UploadServices.class));
                }

                if (DatabaseUtils.getRemoteConfigStatus(this)){
                    Intent intent = new Intent(this,DataService.class);
                    startService(intent);
                }
            }
            
            runThread(150);//1.5 sec
        }

    }

    private void runThread(long sleepTime){

        Thread checkForData = new Thread(){
            @Override
            public void run() {
                super.run();
                try{
                    sleep(sleepTime);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    startActivity(new Intent(SplashActivity.this,
                            MainActivity.class));
                    finish();
                }
            }
        };

        checkForData.start();
    }

    private boolean checkUploadLeft(){

        boolean state = false;

        Cursor cursor = getContentResolver().query(
                MainWordDBContract.Entry.CONTENT_URI,
                new String[]{MainWordDBContract.Entry.COLUMN_UPLOAD},
                MainWordDBContract.Entry.COLUMN_UPLOAD +" =? ",
                new String[]{"0"},
                null);

        if (cursor != null && cursor.getCount() > 0){
            state = true;
        }

        if (cursor != null) {
            cursor.close();
        }


        return state;
    }

    private boolean checkDataBaseStatus(){

        boolean state = false;

        //fixme add a selection option to save time

        Cursor cursor = getContentResolver().query(
                MainWordDBContract.Entry.CONTENT_URI,
                new String[]{MainWordDBContract.Entry._ID},
                null,null,null);

        if (cursor == null || cursor.getCount() == 0){
            DatabaseUtils.initializedDatabase(SplashActivity.this);

        } else if (cursor.getCount() > 0){
            state = true;
        }

        if (cursor != null) {
            cursor.close();
        }


        return state;
    }


}
