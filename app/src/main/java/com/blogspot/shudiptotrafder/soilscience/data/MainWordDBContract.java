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
    public static final String AUTHORITY = "com.blogspot.shudiptotrafder.soilscience";

    // The base content URI = "content://"  <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "tasks" directory
    public static final String PATH_TASKS = "main_ss";

    public static final class MainWordDBEntry implements BaseColumns{

        //content uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_TASKS).build();

        // Task table and column names
        public static final String TABLE_NAME = "main_ss";

        //columns name
        public static final String COLUMN_WORD = "word";
        public static final String COLUMN_DESCRIPTION = "description";
    }

}
