package edu.rosehulman.lewistd.photolifetime;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements LoginFragment.OnLoginListener, ImageListFragment.OnLogoutListener, ImageListFragment.Callback{

    private String[] galleryPermissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PICK_IMAGE =100;
    String mCurrentPhotoPath;
    Uri photoUri;
    FirebaseAuth mAuth;

    FirebaseAuth.AuthStateListener mAuthStateListener;
    Intent mWarningIntent;
    Intent mDeletionIntent;
    AlarmManager alarmManager;
    OnCompleteListener mOnCompleteListener;

    String mUid;
    HashMap<Uri, PendingIntent> warningIntentMap;
    HashMap<Uri, PendingIntent> deletionIntentMap;
    HashMap<Uri, Integer> indexMap;

    int index ;
    private MediaAdapter mAdapter;
    static Context context;
    static ContentResolver contentResolver;


    public static String ARG_GALLARYPIC = "GALLARYPIC";
    public static String ARG_INDEX = "INDEX";
    public String TAG_LIST = "list";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        context = this;
        contentResolver = getContentResolver();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        mAuth = FirebaseAuth.getInstance();
        initializeListeners();

        setSupportActionBar(toolbar);


        //alarm intentv
        index = 0;

        mWarningIntent = new Intent(this, WarningReceiver.class);
        mDeletionIntent = new Intent(this, DeletionReceiver.class);
        warningIntentMap = new HashMap<>();
        deletionIntentMap = new HashMap<>();
        indexMap = new HashMap<>();
        switchToLoginFragment();

    }

    private void initializeListeners() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                Log.d("email", "switch?");
                if(user!=null){
                    mUid = (user.getUid());

                }else{
                    switchToLoginFragment();
                    Log.d("email", "null");
                }
            }
        };
        mOnCompleteListener = new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(!task.isSuccessful()){
                    Log.d("fail", "fail");
                }
            }
        };
    }
    public void switchToLoginFragment(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment, new LoginFragment(), "Login");
        ft.commit();
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

        if(this.warningIntentMap.containsKey(mUri)){
            this.warningIntentMap.get(mUri).cancel();
        }
        this.warningIntentMap.put(mUri, warningPI);

        if(this.deletionIntentMap.containsKey(mUri)){
            this.deletionIntentMap.get(mUri).cancel();
        }
        this.deletionIntentMap.put(mUri, deletionPI);
    }


    @Override
    public void onLogin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()){
                    Log.d("fail", "fail");
                }else{
                    FirebaseUser user = mAuth.getCurrentUser();
                    switchToImageList(user.getUid());
                }
            }
        });
    }

    public void switchToImageList(String mUid){
        Log.d("bilada", "switchoToWeatherpicsList");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment imageListFragment = new ImageListFragment();
        Bundle args = new Bundle();
        args.putString("FIREBASE", mUid);
        imageListFragment.setArguments(args);
        ft.replace(R.id.fragment, imageListFragment, "LIST");
        for(int i=0; i<getSupportFragmentManager().getBackStackEntryCount(); i++){
            getSupportFragmentManager().popBackStackImmediate();
        }
        ft.commit();
    }

    @Override
    public void onLogout() {

        mAuth.signOut();
        switchToLoginFragment();
    }
    protected void onStop(){
        super.onStop();
        if(mAuthStateListener != null){
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
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

    @Override
    public void onDocSelected(Uri mUri) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        Log.d("switch", "switching to galary");
        args.putParcelable("URI", mUri);
        ViewImageFrag gpf = new ViewImageFrag();
        gpf.setArguments(args);
        ft.replace(R.id.fragment, gpf);
        ft.addToBackStack("detail");
        ft.commit();
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
