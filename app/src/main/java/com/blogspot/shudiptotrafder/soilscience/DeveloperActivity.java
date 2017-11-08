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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.shudiptotrafder.soilscience.theme.ThemeUtils;

public class DeveloperActivity extends AppCompatActivity {


    //to support vector drawables for lower api
    static {
        //complete add vector drawable support
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ThemeUtils.initialize(this);

        setContentView(R.layout.activity_developer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView fb = findViewById(R.id.dev_fb);
        TextView linkedin = findViewById(R.id.dev_linkedin);
        TextView git = findViewById(R.id.dev_git);
        TextView email = findViewById(R.id.dev_email);

        fb.setOnClickListener(view -> customTab("https://www.facebook.com/Iamsdt"));

        linkedin.setOnClickListener(view -> customTab("https://www.linkedin.com/in/shudipto-trafder-b041a97a/"));

        git.setOnClickListener(view -> customTab("https://github.com/Iamsdt"));

        email.setOnClickListener(view -> {

            try{
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setData(Uri.parse("email"));
                String[] s = {"Shudiptotrafder@gmail.com"};
                intent.putExtra(Intent.EXTRA_EMAIL,s);
                intent.putExtra(Intent.EXTRA_SUBJECT,"Email from soil science app");
                intent.putExtra(Intent.EXTRA_TEXT,"feedback:");
                intent.setType("message/rfc822");
                startActivity(Intent.createChooser(intent,"Send email with..."));

            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(this,"No email app found",Toast.LENGTH_SHORT).show();
            }

        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void customTab(String url){
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(R.attr.colorPrimary);
        builder.setShowTitle(false);
        // add share action to menu list
        builder.addDefaultShareMenuItem();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(url));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //buy calling android.R.id.home
        int id = item.getItemId();

        if (id == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    //left in this project
    // complete: 6/7/2017 add theme change option
    // complete: 6/7/2017 add search view
    // complete: 6/7/2017 add voice search view
    // complete: 6/7/2017 add random search view
    
    // complete: 6/7/2017 add app intro
    // TODO: 6/7/2017 add item animator 

    //advance
    //complete fill developer with animation

    //database
    // complete: 6/16/2017 Firebase database
    // complete: 6/16/2017 user add word will be real time database

    // complete: 6/16/2017 favourite swap will show a dialog
    // complete: 6/16/2017 night mode move from settings to navigation drawer

    // complete: 8/4/2017 if user open this app for first time then show a import dialog
    // complete: 8/4/2017 Create a file import chooser dialog

}
