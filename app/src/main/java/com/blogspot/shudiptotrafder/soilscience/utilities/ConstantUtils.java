package com.blogspot.shudiptotrafder.soilscience.utilities;

import com.blogspot.shudiptotrafder.soilscience.data.MainWordDBContract;

/**
 * Created by Shudipto on 6/12/2017.
 */

public class ConstantUtils {

    /*SharedPreference Key*/
    public static final String APP_OPEN_FIRST_TIME = "app_open";
    //database key
    public static final String DATABASE_INIT_SP_KEY = "initialized";
    //db backup restore
    public static final String USER_RESTORE = "restore";
    //app intro
    public static final String APP_INTRO_STATUS = "app_intro_status";

    //theme key
    public static final String THEME_SP_KEY = "theme";
    
    //night mode sp key
    public static final String NIGHT_MODE_SP_KEY = "theme";
    public static final String NIGHT_MODE_VALUE_KEY  = "theme";



    /*Database name and id*/
    //database name
    public static final String DB_NAME = "soilscience.db";
    //database version
    public static final int DB_VERSION = 1;

    /*All loaders ids*/
    //main loaders id
    public static final int MAIN_LOADER_ID = 123;
    //Details loader id
    public static final int DETAILS_LOADER_ID = 321;
    //favourite loader id
    public static final int FAVOURITE_LOADER_ID = 132;


    /*Database column select*/
    //selected column form database
    public static final String[] projectionOnlyWord = new String[]
            {MainWordDBContract.Entry.COLUMN_WORD};

    /*Database selected column Index*/
    public static final int INDEX_ONLY_WORD = 0;


    /*Firebase key*/
    //firebase database key for remote config
    public static final String FB_REMOTE_CONFIG_STORAGE_KEY = "storage_key";

    //settings import export
    public static final String SETTING_IMOUT_OPTION_FAVOUTITR = "favourite.txt";
    public static final String SETTING_IMOUT_OPTION_USER = "user.txt";



}
