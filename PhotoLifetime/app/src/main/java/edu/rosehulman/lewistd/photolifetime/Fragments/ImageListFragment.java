package edu.rosehulman.lewistd.photolifetime.Fragments;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.rosehulman.lewistd.photolifetime.*;
import edu.rosehulman.lewistd.photolifetime.R;
import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;

/**
 * Created by parks8 on 5/15/2018.
 */

public class ImageListFragment extends Fragment {

    private String[] galleryPermissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PICK_IMAGE =100;
    String mCurrentPhotoPath;
    Uri photoUri;
    FirebaseAuth mAuth;
    private OnLogoutListener mLogoutListener;


    ImageListFragment.Callback mCallback;
    public MediaAdapter mAdapter;
    static Context context;
    static ContentResolver contentResolver;

    android.support.v4.app.FragmentTransaction mFragmentTransaction;

    public static String ARG_GALLARYPIC = "GALLARYPIC";
    public static String ARG_INDEX = "INDEX";

    public ImageListFragment(){

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("bilada", "in list");
        View rootView = inflater.inflate(edu.rosehulman.lewistd.photolifetime.R.layout.fragment_list, container, false);

        setHasOptionsMenu(true);
        context = getContext();

        BottomNavigationView navigation = (BottomNavigationView) rootView.findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        recyclerView.setHasFixedSize(true);
        Log.d("bilada", "before adapter");

        mAdapter = new MediaAdapter(getActivity(), mCallback, getContext());

        Log.d("bilada", "after adapter");

        recyclerView.setAdapter(mAdapter);

        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case R.id.action_logout:
                mLogoutListener.onLogout();;
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Callback) {
            mCallback = (ImageListFragment.Callback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement Callback");
        }
        mLogoutListener = (OnLogoutListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("bilada", "list onActivity Result  requestcode:" + requestCode);
        switch (requestCode){
            case PICK_IMAGE:
                if (resultCode == RESULT_OK) {
                    photoUri = data.getData();
                    if (photoUri != null) {
                        Log.d("bilada", "gallery opened for :" + photoUri.toString());

                        try {
                            mAdapter.addPic(new Medias(PathUtil.getPath(getContext(), photoUri), -1, mAdapter.getUid()));
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {


                    Medias pic = new Medias(mCurrentPhotoPath, -1, mAdapter.getUid());
                    mAdapter.addPic(pic);
                }
                break;
        }
    }


    private void dispatchTakePictureIntent(){
        Intent takePictureIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getActivity().getPackageManager())!=null){
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
                photoUri = FileProvider.getUriForFile(context,
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
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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


    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }

    public void dispatchVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivity(takeVideoIntent);
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Log.d("selected  ", item.getItemId()+"");
            switch (item.getItemId()) {
                case R.id.photo_button:
//                    mTextMessage.setText(R.string.photo_nav_text);
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());
                    dispatchTakePictureIntent();
                    return true;
                case R.id.video_button:
//                    mTextMessage.setText(R.string.video_nav_text);
                    dispatchVideoIntent();
                    return true;
                case R.id.gallery_button:
//                    mTextMessage.setText(R.string.gallery_nav_text);
                    if (EasyPermissions.hasPermissions(context, galleryPermissions)) {
                        openGallery();
                    } else {
                        EasyPermissions.requestPermissions(context, "Access for storage",
                                101, galleryPermissions);
                    }
//                    openGallery();
                    Log.d("gallery", "gallery opened");
                    return true;
            }
            return false;
        }
    };

    public interface OnLogoutListener {
        void onLogout();
    }

    public interface Callback {
        void onDocSelected(Uri mUri);
    }
}
