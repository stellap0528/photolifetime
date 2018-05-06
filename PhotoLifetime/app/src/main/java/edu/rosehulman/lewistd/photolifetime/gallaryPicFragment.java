package edu.rosehulman.lewistd.photolifetime;

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
    private PhotoLifeTime mCurrentItem;

    int mYear;
    int mDate;
    int mMonth;
    private int index;

    public gallaryPicFragment(){}

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
        mPhotoLifetimeRef = FirebaseDatabase.getInstance().getReference().child("PhotoLifetime");
        mChildEventListener = new PhotoLifeTimeChildEventListener();
        mPhotoLifetimeRef.addChildEventListener(mChildEventListener);
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
                return true;
            case R.id.action_edit_lifetime:
                addEditPhotoLifeTime(true);
                return true;
            case R.id.action_delete:
                showDeleteDialog();
                return true;
        }
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

                long date = deliveryDateView.getDate();
                deliveryDateView.getDate();
                Log.d("alarm", "date selected to :"+deliveryDateView.getDate());

                warningCalendar.set(mYear, mMonth, mDate-1, 0, 0, 0);
                deletionCalendar.set(mYear, mMonth, mDate, 19, 31, 0);
                Log.d("alarm", "date set to :"+deletionCalendar.getTimeInMillis());
                ((MainActivity) getActivity()).setIntentArray(warningCalendar.getTimeInMillis(), deletionCalendar.getTimeInMillis(), mUri);
//                deliveryDateView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
//                    @Override
//                    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
//                        warningCalendar.set(year, month, dayOfMonth-1, 0, 0, 0);
//                        deletionCalendar.set(year, month, dayOfMonth, 0, 0, 0);
//                        Log.d("alarm", "date set to :"+deletionCalendar.getTimeInMillis());
//                        ((MainActivity) getActivity()).setIntentArray(warningCalendar.getTimeInMillis(), deletionCalendar.getTimeInMillis(), mUri);
//                    }
//                });

//                mPhotoLifetimeRef.push().setValue(mUri.toString(), date);
            }
        });
        builder.setNeutralButton("delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((MainActivity) getActivity()).deleteAlarm(mUri);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);

        builder.create().show();
    }
    private void showPhotoLifeTime(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = mInflater.inflate(R.layout.dialog_add, null, false);
        builder.setView(view);
        final CalendarView deliveryDateView = (CalendarView) view.findViewById(R.id.calendar_view);
        builder.setNegativeButton(R.string.done, null);
        deliveryDateView.setDate(mCurrentItem.getTimeStamp());
        builder.create().show();
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


    private class PhotoLifeTimeChildEventListener implements ChildEventListener {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            PhotoLifeTime plt = dataSnapshot.getValue(PhotoLifeTime.class);
            plt.setKey(dataSnapshot.getKey());

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            String key = dataSnapshot.getKey();
            PhotoLifeTime newPLT = dataSnapshot.getValue(PhotoLifeTime.class);

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }
}
