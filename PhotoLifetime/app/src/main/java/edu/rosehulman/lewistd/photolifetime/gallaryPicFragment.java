package edu.rosehulman.lewistd.photolifetime;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;

import android.content.Intent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.widget.CalendarView;
import android.widget.ImageView;

import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by parks8 on 2018-04-30.
 */
public class gallaryPicFragment extends Fragment {

    private Uri mUri;
    private ImageView mGalleryPicView;
    private LayoutInflater mInflater;
    private ChildEventListener mChildEventListener;
    private DatabaseReference mPhotoLifetimeRef;

    private Calendar deletionCalendar = Calendar.getInstance();
    private Calendar warningCalendar = Calendar.getInstance();
    private Context context;

    int mYear;
    int mDate;
    int mMonth;
    private int index;

    public gallaryPicFragment(){}
    private Activity mActivity;
    private MediaAdapter mAdapter;
    AlarmManager alarmManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
        mInflater = inflater;
        Log.d("gallaryFragment", "Fragment opend");
        View rootView = inflater.inflate(R.layout.gallary_pic, container, false);
        mGalleryPicView = rootView.findViewById(R.id.galaray_image_view);
        mUri = getArguments().getParcelable(MainActivity.ARG_GALLARYPIC);
        mGalleryPicView.setImageDrawable(null);
        mGalleryPicView.setImageURI(mUri);
        mActivity = getActivity();
        mAdapter = ((MainActivity)mActivity).getAdapter();
        setHasOptionsMenu(true);
        this.context = getContext();

        index = getArguments().getInt(MainActivity.ARG_INDEX);

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Log.d("menu", "clicked");
        switch(item.getItemId()){
            case R.id.action_view_lifetime:
                showPhotoLifeTime();
                break;
            case R.id.action_edit_lifetime:
                addEditPhotoLifeTime(true);
                break;
            case R.id.action_delete:
                showDeleteDialog();
                break;
        }
        getActivity().getFragmentManager().popBackStack();

        return super.onOptionsItemSelected(item);
    }


    public void showDeleteDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        Log.d("DeleteDialog", "entered");
        builder.setTitle(R.string.delete);
        builder.setMessage(R.string.delete_message);
        builder.setPositiveButton(R.string.delete,  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    deleteImage(mUri);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.create().show();
    }

    private void addEditPhotoLifeTime(boolean isEditing){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = mInflater.inflate(R.layout.dialog_add, null, false);
        builder.setView(view);
        final CalendarView deliveryDateView = (CalendarView) view.findViewById(R.id.calendar_view);

        deliveryDateView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                mYear = year;
                mMonth = month;
                mDate = dayOfMonth;
            }
        });

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                warningCalendar.set(mYear, mMonth, mDate-1, 0, 0, 0);
                deletionCalendar.set(mYear, mMonth, mDate, 2, 49, 0);
                Date date = new Date();

                Log.d("check", "date set to :"+deletionCalendar.getTimeInMillis()+" current time: "+ deliveryDateView.getDate()+"  "+deletionCalendar.getTimeInMillis());
                ((MainActivity) mActivity).setIntentArray(warningCalendar.getTimeInMillis(), deletionCalendar.getTimeInMillis(), mUri);
                Log.d("photopath", mUri.getPath());
                mAdapter.addPic(new Medias(mUri.getPath(), deletionCalendar.getTimeInMillis()));
            }
        });
        builder.setNeutralButton("delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((MainActivity) mActivity).deleteAlarm(mUri);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);

        builder.create().show();
    }
    private void showPhotoLifeTime(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setNegativeButton(R.string.done, null);
        if(mAdapter.containsPic(mUri)!=0){
            View view = mInflater.inflate(R.layout.dialog_add, null, false);
            builder.setView(view);
            final CalendarView deliveryDateView = (CalendarView) view.findViewById(R.id.calendar_view);
            deliveryDateView.setDate(mAdapter.containsPic(mUri));
            builder.create().show();
        }else{
            builder.setTitle("No LifeTime");
            builder.setMessage("This photo does not have lifetime yet");
            builder.setNegativeButton(android.R.string.cancel, null);
            builder.create().show();
        }
    }



    public void deleteImage(Uri pUri) throws URISyntaxException {

        Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Images.Media.DATA + " = ?";
        ContentResolver contentResolver = getActivity().getContentResolver();
        String[] projection = { MediaStore.Images.Media._ID };
        String[] selectionArgs = new String[] {PathUtil.getPath(getContext(), pUri)};
        Cursor c = contentResolver.query(queryUri, projection, selection, selectionArgs, null);
        if(c.moveToFirst()){
            long id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
            Uri deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            contentResolver.delete(deleteUri, null, null);
        }
        c.close();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_edit, menu);
        return;
    }


}
