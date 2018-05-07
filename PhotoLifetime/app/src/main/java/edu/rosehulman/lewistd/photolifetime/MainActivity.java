package edu.rosehulman.lewistd.photolifetime;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PICK_IMAGE =100;
    String mCurrentPhotoPath;
    Uri photoUri;

    Intent mWarningIntent;
    Intent mDeletionIntent;
    AlarmManager alarmManager;
    HashMap<Uri, PendingIntent> warningIntentMap;
    HashMap<Uri, PendingIntent> deletionIntentMap;
    HashMap<Uri, Integer> indexMap;

    int index ;
    private MediaAdapter mAdapter;
    static Context context;
    static ContentResolver contentResolver;


    public static String ARG_GALLARYPIC = "GALLARYPIC";
    public static String ARG_INDEX = "INDEX";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.photo_button:
//                    mTextMessage.setText(R.string.photo_nav_text);
                    dispatchTakePictureIntent();
                    return true;
                case R.id.video_button:
//                    mTextMessage.setText(R.string.video_nav_text);
                    dispatchVideoIntent();
                    return true;
                case R.id.gallery_button:
//                    mTextMessage.setText(R.string.gallery_nav_text);
                    openGallery();
<<<<<<< HEAD
                    Log.d("gallery", "gallery opened");
//                    Bundle args = new Bundle();
//                    args.putParcelable(ARG_GALLARYPIC, photoUri);
//                    gallaryPicFragment gpf = new gallaryPicFragment();
//                    gpf.setArguments(args);
//                    android.app.FragmentTransaction mFragmentTransaction = getFragmentManager().beginTransaction();
//                    mFragmentTransaction.replace(R.id.container_layout, gpf);
//                    mFragmentTransaction.addToBackStack("").commit();
                    
=======
>>>>>>> 839a31c5342abecb38b9e8c077e7b6056c56ce21
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        contentResolver = getContentResolver();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setHasFixedSize(true);
        mAdapter = new MediaAdapter(this, recyclerView, getSupportFragmentManager());
        recyclerView.setAdapter(mAdapter);

        //alarm intentv
        index = 0;

        mWarningIntent = new Intent(this, WarningReceiver.class);
        mDeletionIntent = new Intent(this, DeletionReceiver.class);
        warningIntentMap = new HashMap<>();
        deletionIntentMap = new HashMap<>();
        indexMap = new HashMap<>();

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case PICK_IMAGE:
                if (resultCode == RESULT_OK) {
<<<<<<< HEAD
                    photoUri = data.getData();
                    Bundle args = new Bundle();
                    args.putParcelable(ARG_GALLARYPIC, photoUri);
                    gallaryPicFragment gpf = new gallaryPicFragment();
                    gpf.setArguments(args);
                    android.app.FragmentTransaction mFragmentTransaction = getFragmentManager().beginTransaction();
                    mFragmentTransaction.replace(R.id.container_layout, gpf);
                    mFragmentTransaction.addToBackStack("").commit();

                    File myFile = new File(photoUri.getPath());
                    if (myFile != null) {
                        Medias pic = new Medias(getRealPathFromURI(photoUri));
                        mAdapter.addPic(pic);
=======
                    Uri photoUri = data.getData();

                    if(photoUri!=null) {
                        Log.d("check", "gallery opened for :" + photoUri.toString());
                        switchToGalaryFragment(photoUri);
>>>>>>> 839a31c5342abecb38b9e8c077e7b6056c56ce21
                    }
                }
                break;
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    Medias pic = new Medias(mCurrentPhotoPath);
                    mAdapter.addPic(pic);
                }
                break;
        }
    }

<<<<<<< HEAD
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

=======
    public void switchToGalaryFragment(Uri photoUri){
        Bundle args = new Bundle();
        args.putParcelable(ARG_GALLARYPIC, photoUri);
        gallaryPicFragment gpf = new gallaryPicFragment();
        gpf.setArguments(args);
        android.app.FragmentTransaction mFragmentTransaction = getFragmentManager().beginTransaction();
        mFragmentTransaction.replace(R.id.container_layout, gpf);
        mFragmentTransaction.addToBackStack("").commit();
    }
>>>>>>> 839a31c5342abecb38b9e8c077e7b6056c56ce21
    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    private void dispatchTakePictureIntent(){
        Intent takePictureIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager())!=null){
            // Create the file
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.d("TT", "IO Exception");
            }
            // Continue only if File was successfully created
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this,
                        "com.edu.rosehulman.lewistd.photolifetime.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                galleryAddPic();
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File storageDir =  Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    public void dispatchVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivity(takeVideoIntent);
    }

    public void deleteAlarm(Uri mUri) {
        int index = indexMap.get(mUri);
        alarmManager.cancel(warningIntentMap.get(mUri));
        alarmManager.cancel(deletionIntentMap.get(mUri));
    }

    public void setIntentArray(long warningDate, long deletionDate, Uri mUri){
        if(!indexMap.containsKey(mUri)){
            indexMap.put(mUri, index);
            index++;
        }
        int mIndex = indexMap.get(mUri);

        mWarningIntent.putExtra("URI", mUri);
        mDeletionIntent.putExtra("URI", mUri);

        Log.d("check", "set pendingintent for "+mUri+" Date: "+deletionDate );
//        Log.d("check", )
        PendingIntent warningPI = PendingIntent.getBroadcast(this, mIndex*10, mWarningIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent deletionPI = PendingIntent.getBroadcast(this, mIndex*10+1, mDeletionIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager = (AlarmManager)getSystemService(this.ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, warningDate, warningPI);
        alarmManager.set(AlarmManager.RTC_WAKEUP, deletionDate, deletionPI);

        this.warningIntentMap.put(mUri, warningPI);
        this.deletionIntentMap.put(mUri, deletionPI);
    }

    public static void deleteImage(Uri pUri) throws URISyntaxException {

        Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Images.Media.DATA + " = ?";

        String[] projection = { MediaStore.Images.Media._ID };
        String[] selectionArgs = new String[] {PathUtil.getPath(context, pUri)};
        Cursor c = contentResolver.query(queryUri, projection, selection, selectionArgs, null);
        if(c.moveToFirst()){
            long id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
            Uri deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            contentResolver.delete(deleteUri, null, null);
        }
        c.close();

    }

    /* In-App Camera View Method */
//    /** A safe way to get an instance of the Camera object. */
//    public static Camera getCameraInstance(){
//        Camera c = null;
//        try {
//            c = Camera.open(); // attempt to get a Camera instance
//        }
//        catch (Exception e){
//            // Camera is not available (in use or does not exist)
//        }
//        return c; // returns null if camera is unavailable
//    }

}
