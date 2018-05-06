package edu.rosehulman.lewistd.photolifetime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by parks8 on 5/6/2018.
 */

class DeletionReceiver extends BroadcastReceiver{

    Uri mUri;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("check", "delectionReceiver");

        Intent serviceIntent = new Intent(context, deletionService.class);

        context.startService(serviceIntent);
    }
}
