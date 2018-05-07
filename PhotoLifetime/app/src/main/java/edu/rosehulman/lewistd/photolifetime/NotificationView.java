package edu.rosehulman.lewistd.photolifetime;

import android.app.Activity;
import android.app.NotificationManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;

/**
 * Created by parks8 on 5/6/2018.
 */

public class NotificationView extends Activity {
    private static int IMAGE_MAX_HEIGHT = 1024;
    private static int IMAGE_MAX_WIDTH = 768;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifiaction_view);

        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Dismiss Notification
        notificationmanager.cancel(0);

        // Retrive the data from MainActivity.java
        Intent i = getIntent();

        Uri photoUri= i.getParcelableExtra("URI");

        ImageView photoImage = findViewById(R.id.notificationImage);

        Log.d("check", photoUri.toString());
        Picasso.with(this).load(photoUri).into(photoImage);
//        try {
//            final Bitmap bitmap = decodeFile(new File(photoUri));
//            photoImage.setIma
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private Bitmap decodeFile(File f) throws Exception{
        Bitmap b = null;

        //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;

        FileInputStream fis = new FileInputStream(f);
        BitmapFactory.decodeStream(fis, null, o);
        fis.close();

        int scale = 1;
        int IMAGE_MAX_SIZE=Math.max(IMAGE_MAX_HEIGHT,IMAGE_MAX_WIDTH);
        if (o.outHeight > IMAGE_MAX_HEIGHT || o.outWidth > IMAGE_MAX_WIDTH) {
            scale = (int)Math.pow(2, (int) Math.ceil(Math.log(IMAGE_MAX_SIZE /
                    (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
        }

        //Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        fis = new FileInputStream(f);
        b = BitmapFactory.decodeStream(fis, null, o2);
        fis.close();

        return b;
    }
}
