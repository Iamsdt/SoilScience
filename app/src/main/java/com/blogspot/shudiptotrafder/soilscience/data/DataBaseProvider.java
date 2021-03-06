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

package com.blogspot.shudiptotrafder.soilscience.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;

import com.blogspot.shudiptotrafder.soilscience.R;
import com.blogspot.shudiptotrafder.soilscience.utilities.Utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Shudipto Trafder.
 * on 4/1/2017
 */

class DataBaseProvider {

    /**
     * Class for make database
     * in runtime
     */

    private final Context context;

    DataBaseProvider(Context context) {
        this.context = context;
    }

    /**
     * methods for load words in database
     * with content provider
     *
     */

    void loadWords() {

        final Resources resources = context.getResources();
        //resource i
        int rawId = R.raw.ssnewdb;

        InputStream stream = resources.openRawResource(rawId);

        //read data

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {

            String line;

            while ((line = reader.readLine()) != null) {
                String[] strings = TextUtils.split(line, "=");

                if (strings.length < 1) {
                    continue;
                }

                //those data saved in a array
                //first position for array is word
                //second position for array is description

                ContentValues values = new ContentValues();

                //we use trim() for trim unexpected value
                values.put(MainWordDBContract.Entry.COLUMN_WORD, strings[0].trim());
                values.put(MainWordDBContract.Entry.COLUMN_DESCRIPTION, strings[1].trim());

                //Uri uri = context.getContentResolver().insert(MainWordDBContract.Entry.CONTENT_URI, values);
                context.getContentResolver().insert(MainWordDBContract.Entry.CONTENT_URI, values);

//                if (uri != null) {
//                    //sle("Data status:" + "successfull");
//                }


            }
        } catch (IOException e) {
            Utility.showLogThrowable("Database add error", e);
        }

    }
}
