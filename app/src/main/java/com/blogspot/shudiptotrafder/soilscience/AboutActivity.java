package com.blogspot.shudiptotrafder.soilscience;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AboutActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_READ_STORAGE = 0;
    private static final int PERMISSIONS_REQUEST_WRITE_STORAGE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> {

            if (Build.VERSION.SDK_INT >= 23){

            }


            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        });


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    private void CheckUserPermission(){

        if (Build.VERSION.SDK_INT >= 23){

            List<String> permissionNeed = new ArrayList<>();

            if (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED){
                permissionNeed.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED){
                permissionNeed.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }

            if (permissionNeed.isEmpty()){
                requestPermissions(permissionNeed.toArray(new String[permissionNeed.size()]),
                        PERMISSIONS_REQUEST_READ_STORAGE);
            }

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){

            case PERMISSIONS_REQUEST_READ_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //Permission added
                    Toast.makeText(this, "Thank you", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Sorry You don't Backup" +
                            " your data.To backup your data you need to grant permission", Toast.LENGTH_SHORT).show();
                }
                break;

            case PERMISSIONS_REQUEST_WRITE_STORAGE:

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Thank you", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Sorry You don't Backup" +
                            " your data.To backup your data you need to grant permission", Toast.LENGTH_SHORT).show();
                }
                break;
        }
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
}
