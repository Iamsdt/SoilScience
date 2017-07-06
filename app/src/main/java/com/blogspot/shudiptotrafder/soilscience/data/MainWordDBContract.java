package com.blogspot.shudiptotrafder.soilscience.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * SoilScience
 * com.blogspot.shudiptotrafder.soilscience.data
 * Created by Shudipto Trafder on 4/1/2017.
 */

public class MainWordDBContract {

    // The authority, which is how your code knows which Content Provider to access
    static final String AUTHORITY = "com.blogspot.shudiptotrafder.soilscience";

    // The base content URI = "content://"  <authority>
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "tasks" directory
    static final String PATH_TASKS = "main_ss";

    public static final class Entry implements BaseColumns{

        //content uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_TASKS).build();

        // Task table and column names
        static final String TABLE_NAME = "main_ss";

        //columns name
        public static final String COLUMN_WORD = "word";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_FAVOURITE = "favourite";
        public static final String COLUMN_USER = "user";
        public static final String COLUMN_UPLOAD = "Upload";


        /**
         * build uri with word
         * we send a word(database column from a particular row) and its build
         *
         * @param s word from a selected item in Recycler view and make uri
         *          to send to details activity
         * @return new uri with given word
         */

        public static Uri buildUriWithWord(String s) {
            return CONTENT_URI.buildUpon().appendPath(s).build();
        }

    }

}
