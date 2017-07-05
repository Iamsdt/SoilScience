package com.blogspot.shudiptotrafder.soilscience;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.blogspot.shudiptotrafder.soilscience.data.DatabaseUtils;
import com.blogspot.shudiptotrafder.soilscience.utilities.Utility;

/**
 * Created by Shudipto on 7/6/2017.
 */

public class DataService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Utility.showLog("Service called");
        DatabaseUtils.addRemoteData(this);

        return super.onStartCommand(intent, flags, startId);
    }
}
