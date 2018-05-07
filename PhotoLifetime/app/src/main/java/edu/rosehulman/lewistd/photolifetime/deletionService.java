//package edu.rosehulman.lewistd.photolifetime;
//
//import android.app.Notification;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.app.Service;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.os.IBinder;
//import android.support.annotation.Nullable;
//import android.util.Log;
//
///**
// * Created by parks8 on 5/6/2018.
// */
//
//public class deletionService extends Service {
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId){
//        Log.d("check", "deletionService");
//
//        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        Intent mainActivityIntent = new Intent(this.getApplicationContext(), MainActivity.class);
//
//        PendingIntent pendingintent = PendingIntent.getActivity(this, 0, mainActivityIntent, 0);
//
//        Notification popup = new Notification.Builder(this).setContentTitle("Photo is deleted").setContentIntent(pendingintent).setSmallIcon(R.drawable.ic_access_time).setAutoCancel(true).build();
//
//        notificationManager.notify(0, popup);
//
//        return START_NOT_STICKY;
//    }
//}
