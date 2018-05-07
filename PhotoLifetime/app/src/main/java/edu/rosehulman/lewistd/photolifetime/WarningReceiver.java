package edu.rosehulman.lewistd.photolifetime;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;

/**
 * Created by parks8 on 5/6/2018.
 */

public class WarningReceiver extends BroadcastReceiver{

    Uri mUri;

    @Override
    public void onReceive(Context context, Intent intent) {

        mUri = intent.getParcelableExtra("URI");
        Log.d("check", "warningReceiver: " + mUri);

        try {
            Notification(context, mUri);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void Notification(Context context, Uri mUri) throws IOException, URISyntaxException {
        Intent intent = new Intent(context, NotificationView.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), mUri);

        Log.d("check", "warning uri: "+mUri);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_access_time)
                .setLargeIcon(bitmap)
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap).bigLargeIcon(null))
                .setContentTitle("Picture will be deleted tomorrow")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE), builder.build());
    }

}
