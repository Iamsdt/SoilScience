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

package com.blogspot.shudiptotrafder.soilscience.utilities;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.blogspot.shudiptotrafder.soilscience.data.BaseDataStructure;
import com.blogspot.shudiptotrafder.soilscience.data.DatabaseUtils;
import com.blogspot.shudiptotrafder.soilscience.data.MainWordDBContract;
import com.codekidlabs.storagechooser.utils.DiskUtil;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Class for helping file input and output
 * from user settings */
public class FileImportExportUtils {

    /**
     * Export user favourite data
     * export in a text file just favourite word only
     * @param context for access sp */
    public static void exportFileFavourite(Context context) {

        ArrayList<String> arrayList = new ArrayList<>();

        //database agr
        String selection = MainWordDBContract.Entry.COLUMN_FAVOURITE + " = ? ";
        String[] selectionArg = new String[]{"1"};

        String[] projection = new String[]{MainWordDBContract.Entry.COLUMN_WORD};

        Cursor cursor = context.getContentResolver().query(
                MainWordDBContract.Entry.CONTENT_URI,
                projection, selection, selectionArg, null);


        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            do {
                arrayList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        if (arrayList.isEmpty()) {
            Toast.makeText(context, "Sorry you don't have any data to save",
                    Toast.LENGTH_SHORT).show();
        } else {
            //else means user have favourite word

            //getting user backup path
            SharedPreferences sharedPreferences = context
                    .getSharedPreferences(ConstantUtils.STORAGE_PATH_KEY, Context.MODE_PRIVATE);
            String path = sharedPreferences.getString(DiskUtil.SC_PREFERENCE_KEY,
                    ConstantUtils.DEFAULT_PATH_STORAGE);

            boolean directoryStatus = false;

            File dir = new File(path);

            //First check directory is exist or not
            //if directory not exist create a new one
            if (!dir.exists()) {
                directoryStatus = dir.mkdirs();
            } else if (dir.exists()) {
                directoryStatus = true;
            }

            //if directory is exist then do next
            if (directoryStatus) {

                //create a new file
                File file = new File(dir,
                        ConstantUtils.SETTING_IMOUT_OPTION_FAVOURITE);

                try {

                    //if file is not exist
                    //then create new one
                    if (!file.exists()) {
                        file.createNewFile();
                        file.setWritable(true);
                    } else {
                        //if already exist the set it as a write able
                        file.setWritable(true);
                    }

                    //output stream
                    FileOutputStream outputStream = new FileOutputStream(file);
                    for (String word : arrayList) {
                        //adding a new line after every word
                        String savedStr = word + "\n";
                        outputStream.write(savedStr.getBytes());
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "File is not created successfully",
                            Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Something went wrong",
                            Toast.LENGTH_SHORT).show();
                } finally {
                    if (file.exists() && file.canRead()) {
                        Toast.makeText(context, "your favourite word list saved in the "
                                        + path +ConstantUtils.SETTING_IMOUT_OPTION_FAVOURITE
                                        + " directory",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }


        }
    }

    /**
     * Export user added data
     * export in a text file just favourite word only
     * @param context for access sp
     */
    public static void exportFileUser(Context context) {

        ArrayList<BaseDataStructure> arrayList = new ArrayList<>();

        //database agr
        String selection = MainWordDBContract.Entry.COLUMN_USER + " = ? ";
        String[] selectionArg = new String[]{"1"};

        String[] projection = new String[]{MainWordDBContract.Entry.COLUMN_WORD,
                MainWordDBContract.Entry.COLUMN_DESCRIPTION};

        Cursor cursor = context.getContentResolver().query(
                MainWordDBContract.Entry.CONTENT_URI,
                projection, selection, selectionArg, null);


        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            do {
                arrayList.add(new BaseDataStructure(cursor.getString(0),
                        cursor.getString(1)));

            } while (cursor.moveToNext());

        }

        if (cursor != null) {
            cursor.close();
        }

        if (arrayList.isEmpty()) {
            Toast.makeText(context, "Sorry you don't have any data to save",
                    Toast.LENGTH_SHORT).show();
        } else {
            //else means user have favourite word

            //getting user backup path
            SharedPreferences sharedPreferences = context
                    .getSharedPreferences(ConstantUtils.STORAGE_PATH_KEY, Context.MODE_PRIVATE);
            String path = sharedPreferences.getString(DiskUtil.SC_PREFERENCE_KEY,
                    ConstantUtils.DEFAULT_PATH_STORAGE);

            File dir = new File(path);

            boolean dirStatus = false;

            //First check directory is exist or not
            //if directory not exist create a new one
            if (!dir.exists()) {
                dirStatus = dir.mkdirs();

            } else if (dir.exists()) {
                dirStatus = true;
            }

            //if directory is exist then do next
            if (dirStatus) {

                //create a new file
                File file = new File(dir,
                        ConstantUtils.SETTING_IMOUT_OPTION_USER);
                try {

                    //if file is not exist
                    //then create new one
                    if (!file.exists()) {
                        file.createNewFile();
                        file.setWritable(true);
                    } else {
                        //if already exist the set it as a write able
                        file.setWritable(true);
                    }

                    FileOutputStream outputStream = new FileOutputStream(file);

                    for (BaseDataStructure dataStructure : arrayList) {
                        String savedStr = dataStructure.getWord() + "="
                                + dataStructure.getDescription() + "\n";
                        outputStream.write(savedStr.getBytes());
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (file.exists() && file.canRead()) {
                        Toast.makeText(context, "your added word list saved in the "
                                        + path+ ConstantUtils.SETTING_IMOUT_OPTION_USER + " directory",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

    }

    //import file and added to database
    public static void importFile(Context context, String path) {

        if (path == null) {
            Toast.makeText(context, "File not selected correctly", Toast.LENGTH_SHORT).show();
            return;
        } else if (!path.contains(".txt")){
            Toast.makeText(context, "Please input text file only", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(path);

        //contain inserted data uri
        ArrayList<Uri> inserted = new ArrayList<>();
        //contain updated number
        ArrayList<Integer> updated = new ArrayList<>();

        try {
            DataInputStream dataInputStream =
                    new DataInputStream(new FileInputStream(file));

            BufferedReader reader = new BufferedReader
                    (new InputStreamReader(dataInputStream));

            String line;

            while ((line = reader.readLine()) != null) {

                if (line.contains("=")) {
                    //that means it found user added data
                    String[] strings = TextUtils.split(line, "=");

                    if (strings.length < 1) {
                        continue;
                    }

                    ContentValues values = new ContentValues();

                    //use trim() for trim unexpected value
                    values.put(MainWordDBContract.Entry.COLUMN_WORD, strings[0].trim());
                    values.put(MainWordDBContract.Entry.COLUMN_DESCRIPTION, strings[1].trim());
                    values.put(MainWordDBContract.Entry.COLUMN_USER, true);

                    Uri uri = context.getContentResolver()
                            .insert(MainWordDBContract.Entry.CONTENT_URI, values);

                    inserted.add(uri);

                } else {

                    if (DatabaseUtils.checkDataExist(context, line)) {
                        //word exist
                        //now update the data just favourite column
                        Uri mUri = MainWordDBContract.Entry.buildUriWithWord(line);
                        ContentValues values = new ContentValues();
                        values.put(MainWordDBContract.Entry.COLUMN_FAVOURITE, true);
                        int num = context.getContentResolver().update(mUri, values,
                                null, null);

                        updated.add(num);

                    }
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context, "File not exists", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Select correct file", Toast.LENGTH_SHORT).show();
        } finally {

            if (!inserted.isEmpty()) {
                Toast.makeText(context, inserted.size() + " word added", Toast.LENGTH_SHORT).show();
            }

            if (!updated.isEmpty()) {
                Toast.makeText(context, updated.size() + " favourite word added", Toast.LENGTH_SHORT).show();
            }
        }

    }

    /**
     * Checking any file is available or not
     * if available then import it
     * if for first time app open
     * if user uninstall the app and reinstall again the it will work */

    // TODO: 11/29/2017 add with backup agent or auto backup latter
    public static void checkFileAvailable(Context context) {

        try {
            /* only checking the default directory
             * if user saved on other location
             * it will not execute */
            File favourite = new File(ConstantUtils.DEFAULT_PATH_STORAGE,
                    ConstantUtils.SETTING_IMOUT_OPTION_FAVOURITE);

            File user = new File(ConstantUtils.DEFAULT_PATH_STORAGE,
                    ConstantUtils.SETTING_IMOUT_OPTION_USER);

            if ((favourite.exists() && favourite.canRead()) ||
                    (user.exists() && user.canRead())) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle("Restore Previous data")
                        .setMessage("You have some previous data that saved in your storage.\n" +
                                "Do you want to restore your all data?")
                        .setPositiveButton("yes", (dialog, which) -> new Thread(() -> {
                            //import user
                            importFile(context, ConstantUtils.STORAGE_PATH + ConstantUtils.SETTING_IMOUT_OPTION_USER);

                            //import favourite
                            importFile(context, ConstantUtils.STORAGE_PATH + ConstantUtils.SETTING_IMOUT_OPTION_USER);
                        }).start())
                        .setNegativeButton("skip", (dialog, which) -> {
                            //nothing to do
                        });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
