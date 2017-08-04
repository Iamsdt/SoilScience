package com.blogspot.shudiptotrafder.soilscience.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;

import com.blogspot.shudiptotrafder.soilscience.data.BaseDataStructure;
import com.blogspot.shudiptotrafder.soilscience.data.DatabaseUtils;
import com.blogspot.shudiptotrafder.soilscience.data.MainWordDBContract;

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
 * Created by Shudipto on 7/17/2017.
 */

public class FileImportExportUtills {

    private static String path = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/Android/data/";

    private static String SSData = "/ssData";

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

            boolean directoryStatus = false;

            File dir = new File(path+context.getPackageName()+SSData);
            if (!dir.exists()) {
                directoryStatus = dir.mkdirs();
            } else if (dir.exists()){
                directoryStatus = true;
            }

            if (directoryStatus) {

                File file = new File(dir,
                        ConstantUtils.SETTING_IMOUT_OPTION_FAVOUTITR);

                try {

                    if (!file.exists()) {
                        file.createNewFile();
                    }

                    //already file exists so we need to overwrite
                    if (file.exists()) {

                        FileOutputStream outputStream = new FileOutputStream(file);
                        for (String word : arrayList) {
                            String savedStr = word + "\n";
                            outputStream.write(savedStr.getBytes());
                        }

                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (file.exists() && file.canRead()) {
                        showToast(context);
                    }
                }
            }

        }

    }

    private static void showToast(Context context){
        Toast.makeText(context, "your favourite list saved in the "
                        + path+context.getPackageName()+SSData + ConstantUtils.SETTING_IMOUT_OPTION_FAVOUTITR + " directory",
                Toast.LENGTH_SHORT).show();
    }

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

            File dir = new File(path + context.getPackageName() + SSData);

            boolean dirStatus = false;

            if (!dir.exists()) {
                dirStatus = dir.mkdirs();

            } else if (dir.exists()) {
                dirStatus = true;
            }

            if (dirStatus) {

                File file = new File(dir,
                        ConstantUtils.SETTING_IMOUT_OPTION_USER);
                try {
                    FileOutputStream outputStream = new FileOutputStream(file);

                    for (BaseDataStructure dataStructure : arrayList) {
                        String savedStr = dataStructure.getWord() + "="
                                + dataStructure.getDescription() + "\n"+"\n";
                        outputStream.write(savedStr.getBytes());
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                finally {
                    if (file.exists() && file.canRead()) {
                        showToast(context);
                    }
                }
            }
        }

    }

    public static void importFile(Context context, String type) {

        File file = new File(path+context.getPackageName()+SSData, type);

        ArrayList<Uri> inserted = new ArrayList<>();
        ArrayList<Integer> updated = new ArrayList<>();

        try {
            DataInputStream dataInputStream =
                    new DataInputStream(new FileInputStream(file));

            BufferedReader reader = new BufferedReader(new InputStreamReader(dataInputStream));

            String line;

            while ((line = reader.readLine()) != null) {

                if (line.contains("=")) {

                    String[] strings = TextUtils.split(line, "=");

                    if (strings.length < 1) {
                        continue;
                    }

                    ContentValues values = new ContentValues();

                    //we use trim() for trim unexpected value
                    values.put(MainWordDBContract.Entry.COLUMN_WORD, strings[0].trim());
                    values.put(MainWordDBContract.Entry.COLUMN_DESCRIPTION, strings[1].trim());
                    values.put(MainWordDBContract.Entry.COLUMN_USER, true);

                    Uri uri= context.getContentResolver()
                            .insert(MainWordDBContract.Entry.CONTENT_URI, values);

                    inserted.add(uri);

                } else {

                    if (DatabaseUtils.checkDataExist(context, line)) {
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
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            if (!inserted.isEmpty()){
                Toast.makeText(context, inserted.size()+" word added", Toast.LENGTH_SHORT).show();
            }

            if (!updated.isEmpty()){
                Toast.makeText(context, updated.size()+" favourite word added", Toast.LENGTH_SHORT).show();
            }
        }

    }

}
