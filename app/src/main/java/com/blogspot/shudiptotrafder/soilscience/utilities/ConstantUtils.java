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

import android.os.Environment;

import com.blogspot.shudiptotrafder.soilscience.data.MainWordDBContract;

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
    public static final String NIGHT_MODE_SP_KEY = "NightMode";
    public static final String NIGHT_MODE_VALUE_KEY  = "nightValue";



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
    static final String SETTING_IMOUT_OPTION_FAVOURITE = "favourite.txt";
    static final String SETTING_IMOUT_OPTION_USER = "user.txt";


    public static final String DEFAULT_PATH_STORAGE = Environment.getExternalStorageDirectory()
            .getAbsolutePath()+"/SSDictionary/";

    public static final String STORAGE_PATH_KEY = "storage";
    static final String STORAGE_PATH = "path";


}
