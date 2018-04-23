package edu.rosehulman.lewistd.photolifetime;

import android.content.Intent;
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
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PICK_IMAGE =100;
    String mCurrentPhotoPath;
    Uri photoUri;

    private ImageView mImageView;
    private MediaAdapter mAdapter;

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
                    return true;
            }
            return false;
        }
    };

    public boolean onContextItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.action_view_lifetime:
                return true;
            case R.id.action_edit_lifetime:
                return true;
            case R.id.action_delete:
                return true;
        }
        return super.onContextItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
//        mImageView = findViewById(R.id.imageView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
//        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
//        recyclerView.setHasFixedSize(true);
//        mAdapter = new MediaAdapter(this, recyclerView);
//        recyclerView.setAdapter(mAdapter);

        /* In-App Camera View Method */
//        if (MainActivity.this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
//
//            // this device has a camera
//            getCameraInstance();
//        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case PICK_IMAGE:
                if (resultCode == RESULT_OK) {
                    photoUri = data.getData();
//                    ImageView imageView = findViewById(R.id.imageView);
//                    imageView.setImageURI(imageUri);
//            imageView.setImageURI(imageUri);
                }
                break;
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {
//                    Bundle extras = data.getExtras();
//                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Medias pic = new Medias(bitmap, mCurrentPhotoPath);
//                    mAdapter.addPic(pic);
                }
                break;
        }
    }

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

    public static  Bitmap cropAndScale (Bitmap source,int scale){
        int factor = source.getHeight() <= source.getWidth() ? source.getHeight(): source.getWidth();
        int longer = source.getHeight() >= source.getWidth() ? source.getHeight(): source.getWidth();
        int x = source.getHeight() >= source.getWidth() ?0:(longer-factor)/2;
        int y = source.getHeight() <= source.getWidth() ?0:(longer-factor)/2;
        source = Bitmap.createBitmap(source, x, y, factor, factor);
        source = Bitmap.createScaledBitmap(source, scale, scale, false);
        return source;
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
