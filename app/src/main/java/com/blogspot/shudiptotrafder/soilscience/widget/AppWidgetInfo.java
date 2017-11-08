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

package com.blogspot.shudiptotrafder.soilscience.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.blogspot.shudiptotrafder.soilscience.DetailsActivity;
import com.blogspot.shudiptotrafder.soilscience.MainActivity;
import com.blogspot.shudiptotrafder.soilscience.R;
import com.blogspot.shudiptotrafder.soilscience.SearchActivity;
import com.blogspot.shudiptotrafder.soilscience.data.MainWordDBContract;

import java.util.Random;

public class AppWidgetInfo extends AppWidgetProvider {

    public static final String ACTION = "com.blogspot.shudiptotrafder.soilscience.widget";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, int[] appWidgetIds) {

        String word = "word";
        String des = "des";

        //for select data column from database
        final String[] projection = {
                MainWordDBContract.Entry.COLUMN_WORD,
                MainWordDBContract.Entry.COLUMN_DESCRIPTION
        };

        Cursor cursor = context.getContentResolver().query(
                MainWordDBContract.Entry.CONTENT_URI,
                projection,
                null,null,null
                );


        if (cursor != null && cursor.getCount() > 0){

            int size = cursor.getCount();

            Random random = new Random();
            int position = random.nextInt(size);
            if (position == 0){
                cursor.moveToPosition(1);
            } else {
                cursor.moveToPosition(position);
            }

            try{
                word = cursor.getString(0);
                des = cursor.getString(1);
            } catch (Exception e){
                e.printStackTrace();
                //todo crash report
            } finally {
                cursor.close();
            }

        }

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);
        views.setTextViewText(R.id.widget_word,word);
        views.setTextViewText(R.id.widget_des,des);

        Intent wordIntent = new Intent(context,DetailsActivity.class);
        wordIntent.setData(MainWordDBContract.Entry.buildUriWithWord(word));

        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,wordIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);


        //for app icon
        Intent appIntent = new Intent(context,MainActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context,157,appIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.app_widget_icon, appPendingIntent);

        //for tv
        PendingIntent tvPendingIntent = PendingIntent.getActivity(context,175,appIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.app_widget_TV, tvPendingIntent);

        //for search btn
        Intent searchIntent = new Intent(context,SearchActivity.class);
        PendingIntent searchPendingIntent = PendingIntent.getActivity(context,177,searchIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.app_widget_SearchBtn, searchPendingIntent);

        //for refresh
        Intent refreshIntent = new Intent(context,AppWidgetInfo.class);
        refreshIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetId);


        PendingIntent refreshPendingIntent = PendingIntent.getActivity(context,197,refreshIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.app_widget_refreshBtn, refreshPendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId,appWidgetIds);
        }

    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        //super.onReceive(context, intent);

        if (intent.getAction() != null && intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                int[] appWidgetIds = extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS);
                if (appWidgetIds != null && appWidgetIds.length > 0) {
                    //Toast.makeText(context, "Refreshing", Toast.LENGTH_SHORT).show();
                    this.onUpdate(context, AppWidgetManager.getInstance(context), appWidgetIds);
                }
            }
        }

    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

