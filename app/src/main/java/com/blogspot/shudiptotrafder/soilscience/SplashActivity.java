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

package com.blogspot.shudiptotrafder.soilscience;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.blogspot.shudiptotrafder.soilscience.data.DatabaseUtils;
import com.blogspot.shudiptotrafder.soilscience.data.MainWordDBContract;
import com.blogspot.shudiptotrafder.soilscience.theme.ThemeUtils;
import com.blogspot.shudiptotrafder.soilscience.utilities.ConstantUtils;

public class SplashActivity extends AppCompatActivity {

    //to support vector drawables for lower api
    static {
        //complete add vector drawable support
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Utility.setNightMode(this);
        ThemeUtils.initialize(this);
        super.onCreate(savedInstanceState);

//        boolean introStatus = preferences.getBoolean(ConstantUtils.APP_INTRO_STATUS,
//                false);
//
//        if (!introStatus){
//            startActivity(new Intent(this,MyAppIntro.class));
//            finish();
//        }

        setContentView(R.layout.activity_splash);

        ImageView imageView = (ImageView) findViewById(R.id.splash_imageView);
        TextView app = (TextView) findViewById(R.id.splash_app);
        TextView appAbout = (TextView) findViewById(R.id.splash_appAbout);


        Animation set = AnimationUtils.loadAnimation(this,
                R.anim.splash_screen_animation);

        //set animation
        imageView.setAnimation(set);
        app.setAnimation(set);
        appAbout.setAnimation(set);

        //start animation
        set.start();

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle(getString(R.string.splash_pd_title));
        dialog.setMessage(getString(R.string.splash_pd_meg));
        dialog.setCancelable(false);

        //SharedPreferences for database initializing state
        // for first time value
        SharedPreferences preferences = getSharedPreferences(
                ConstantUtils.APP_OPEN_FIRST_TIME, MODE_PRIVATE);
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
                runThread(1000);//1s
            }
        } else {

            runThread(1500);//1.5 sec
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

    private boolean checkDataBaseStatus(){

        boolean state = false;

        //complete add a selection option to save time

        String[] selectionArg = new String[]{"%"+"1"+"%"};

        Cursor cursor = getContentResolver().query(
                MainWordDBContract.Entry.CONTENT_URI,
                new String[]{MainWordDBContract.Entry._ID},
                MainWordDBContract.Entry._ID + " like ? ",selectionArg,null);

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
