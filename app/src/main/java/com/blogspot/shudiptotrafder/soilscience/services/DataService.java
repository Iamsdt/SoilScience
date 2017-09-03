package com.blogspot.shudiptotrafder.soilscience.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.blogspot.shudiptotrafder.soilscience.data.DatabaseUtils;
import com.blogspot.shudiptotrafder.soilscience.utilities.Utility;

/**
 * Created by Shudipto on 7/6/2017.
 */

public class DataService extends IntentService {

    public DataService() {
        super("DATA_SERVICES");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Utility.showLog("Service called");
        DatabaseUtils.addRemoteData(this);
    }
}
