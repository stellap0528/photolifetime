package edu.rosehulman.lewistd.photolifetime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by parks8 on 5/6/2018.
 */

public class WarningReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("check", "In receiver");
    }
}
