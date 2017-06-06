package com.blogspot.shudiptotrafder.soilscience.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.blogspot.shudiptotrafder.soilscience.BuildConfig;
import com.blogspot.shudiptotrafder.soilscience.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * SoilScience
 * com.blogspot.shudiptotrafder.soilscience.data
 * Created by Shudipto Trafder on 4/1/2017.
 */

public class DataBaseProvider {

    /**
     * Class for make database
     * in runtime
     */

    private Context context;

    public DataBaseProvider(Context context) {
        this.context = context;
    }

//    public void loadDictionary(){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try{
//                    loadWords();
//                } catch (IOException e){
//                    slet("Error in loadind Dictionary on " +
//                            "Called methods loadwords",e);
//                    throw new RuntimeException(e);
//                }
//            }
//        }).start();
//    }


    /** methods for load words in database
     *  with content provider
     * @throws IOException if file not found in raw
     */

    public void loadWords() throws IOException{

        if (BuildConfig.DEBUG){
            Log.i("DataBaseProvider","Words start loading");
        }

        final Resources resources = context.getResources();
        //resource i
        int rawId = R.raw.ssnewdb;

        InputStream stream = resources.openRawResource(rawId);

        //read data
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        try{
            String line;

            while ((line = reader.readLine())!= null){
                String[] strings = TextUtils.split(line,"=");

                if (strings.length < 1) {
                    continue;
                }

                //those data saved in a array
                //first position for array is word
                //second position for array is description

                ContentValues values = new ContentValues();

                //we use trim() for trim unexpected value
                values.put(MainWordDBContract.Entry.COLUMN_WORD,strings[0].trim());
                values.put(MainWordDBContract.Entry.COLUMN_DESCRIPTION,strings[1].trim());

                Uri uri = context.getContentResolver().insert(MainWordDBContract.Entry.CONTENT_URI,values);

                if (uri != null) {
                    sle("Data status:" + "successfull");
                }


            }
        } catch (IOException e){
            slet("Error in load word",e);
        } finally {
            reader.close();
        }

    }

    /**
     * log message methods that's display log only debug mode
     *
     * @param string message that to display
     */
    private static void sle(String string) {
        //show log with error message
        //if debug mode enable
        String Tag = "DataBaseProvider";

        if (BuildConfig.DEBUG) {
            Log.e(Tag, string);
        }
    }

    /**
     * log message methods that's display log only debug mode
     *
     * @param s message that to display
     * @param t throwable that's throw if exception happen
     */
    private static void slet(String s, Throwable t) {
        //show log with error message with throwable
        //if debug mode enable
        String Tag = "DataBaseProvider";

        if (BuildConfig.DEBUG) {
            Log.e(Tag, s, t);
        }
    }

}
