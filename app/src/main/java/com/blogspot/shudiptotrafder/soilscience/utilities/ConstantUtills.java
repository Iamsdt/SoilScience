package com.blogspot.shudiptotrafder.soilscience.utilities;

import com.blogspot.shudiptotrafder.soilscience.data.MainWordDBContract;

/**
 * Created by Shudipto on 6/12/2017.
 */

public class ConstantUtills {
    //sp key
    public static final String DATABASE_INIT_SP_KEY = "initialized";

    //database
    public static final String DB_NAME = "soilscience.db";
    public static final int DB_VERSION = 1;

    //all loaders id
    //main loaders id
    public static final int MAIN_LOADER_ID = 0;
    //Details loader id
    public static final int DETAILS_LOADER_ID = 321;
    //favourite loader id
    //Loader id
    public static final int FAVOURITE_LOADER_ID = 122;


    //selected column form database
    public static final String[] projectionOnlyWord = new String[]
            {MainWordDBContract.Entry.COLUMN_WORD};

    //for words index
    public static final int INDEX_ONLY_WORD = 0;
}
