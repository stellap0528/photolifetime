package edu.rosehulman.lewistd.photolifetime;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;

import static edu.rosehulman.lewistd.photolifetime.MainActivity.ARG_GALLARYPIC;

/**
 * Created by parks8 on 5/6/2018.
 */

public class DeletionReceiver extends BroadcastReceiver{

    Uri mUri;

    @Override
    public void onReceive(Context context, Intent intent) {

        mUri = intent.getParcelableExtra("URI");
        Log.d("check", "delectionReceiver: " + mUri);

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

        Log.d("check", "uri: "+mUri);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_access_time)
                .setLargeIcon(bitmap)
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap).bigLargeIcon(null))
                .setContentTitle("Picture Deleted")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE), builder.build());
        MainActivity.deleteImage(mUri);
        MainActivity.getAdapter().deleteMedia(mUri);
    }


}
