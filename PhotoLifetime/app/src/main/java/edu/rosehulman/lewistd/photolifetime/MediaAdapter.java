package edu.rosehulman.lewistd.photolifetime;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder> {

    int mYear,mMonth,mDate;
    Context mContext;
    ArrayList<Medias> mGallery;
    RecyclerView mRecyclerView;
    Medias mCurrentMedia;
    String mUid;

    private Calendar deletionCalendar = Calendar.getInstance();
    private Calendar warningCalendar = Calendar.getInstance();
    DatabaseReference mPhotoLifetimeRef;
    Query mQueryRef;
    FragmentManager mFM;
    LayoutInflater mInflater;
    Activity mActivity;
    ImageListFragment.Callback mCallback;
//    ArrayList<Medias> mMedias;

    public MediaAdapter(Context context, ImageListFragment.Callback callback) {
//        mActivity = activity;
        mContext = context;
        mCallback = callback;
//        mRecyclerView = rView;
        mGallery = new ArrayList<>();
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        mUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mPhotoLifetimeRef = FirebaseDatabase.getInstance().getReference().child("weatherpics");
        
        mQueryRef = mPhotoLifetimeRef.orderByChild("uid").equalTo(mUid);
//        mGallery.mMedia.add(new Medias());
    }



//    public MediaAdapter(Context context, RecyclerView rView, FragmentManager fm, ArrayList<Medias> newGallery) {
//        mContext = context;
//        mRecyclerView = rView;
//        mGallery = new ArrayList<>();
//        mFM = fm;
//        mGallery = newGallery;
////        mGallery.mMedia.add(new Medias());
//    }

    class MediaEventListener implements ChildEventListener{

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.media_view, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Medias currentMedia = mGallery.get(position);
        holder.mImageView.setImageURI(mGallery.get(position).mediaPath);
    }

    @Override
    public int getItemCount() {
        return mGallery.size();
    }

    public void addPic(Medias picture) {
        mGallery.add(picture);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;

        public ViewHolder(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onDocSelected(mGallery.get(getAdapterPosition()).getMediaPath());
//                    ViewImageFrag mImageFragment = new ViewImageFrag().newInstance(mGallery.get(getAdapterPosition()).mediaPath);
//                    FragmentTransaction ft = mFM.beginTransaction();
//                    ft.replace(R.id.container_layout, mImageFragment);
//                    ft.addToBackStack("image");
//                    ft.commit();
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mCurrentMedia = mGallery.get(getAdapterPosition());
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle(R.string.dialog_title);
                    builder.setItems(R.array.dialog_array, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    showPhotoLifeTime(mCurrentMedia.getMediaPath());
                                    break;
                                case 1:
                                    addEditPhotoLifeTime(mCurrentMedia.getMediaPath());
                                    break;
                                case 2:
                                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                    builder.setTitle(R.string.safety_delete_dialog);
                                    builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            deleteMedia(getAdapterPosition());
                                            Snackbar.make(itemView, "Item Deleted", Snackbar.LENGTH_SHORT)
                                                    .show();
                                        }
                                    });
                                    builder.setNegativeButton(android.R.string.no, null);
                                    builder.create().show();
                                    break;
                                case 3:
                                    Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                                    Uri myFilePath = mGallery.get(getAdapterPosition()).mediaPath;


                                        intentShareFile.setType("image/jpeg");
                                        intentShareFile.putExtra(Intent.EXTRA_STREAM, myFilePath);

                                        intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                                                "Sharing File...");
                                        intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");

                                        mContext.startActivity(Intent.createChooser(intentShareFile, "Share File"));

                                    break;
                            }

                        }
                    });
                    builder.setNegativeButton(android.R.string.cancel, null);
                    builder.create().show();
                    return false;
                }
            });
            mImageView = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }

    private void addEditPhotoLifeTime(final Uri mUri){
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext);
        View view = mInflater.inflate(R.layout.dialog_add, null, false);
        builder.setView(view);
        final CalendarView deliveryDateView = (CalendarView) view.findViewById(R.id.calendar_view);


        if(this.containsPic(mUri)!=-1){
            deliveryDateView.setDate(this.containsPic(mUri));
        }
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

                    setPicTime(mUri, deletionCalendar.getTimeInMillis());
            }
        });
        builder.setNeutralButton("delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((MainActivity) mActivity).deleteAlarm(mUri);
                setPicTime(mUri, -1);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);

        builder.create().show();
    }

    private void showPhotoLifeTime(Uri mUri){
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext);
        builder.setNegativeButton(R.string.done, null);
        if(this.containsPic(mUri)!=-1){
            View view = mInflater.inflate(R.layout.dialog_add, null, false);
            builder.setView(view);
            final CalendarView deliveryDateView = (CalendarView) view.findViewById(R.id.calendar_view);
            deliveryDateView.setDate(this.containsPic(mUri));
            builder.create().show();
        }else{
            builder.setTitle("No LifeTime");
            builder.setMessage("This photo does not have lifetime yet");
            builder.setNegativeButton(android.R.string.cancel, null);
            builder.create().show();
        }
    }

    private void deleteMedia(int adapterPosition) {
        mGallery.remove(adapterPosition);
        ((MainActivity)mActivity).deleteAlarm(mGallery.get(adapterPosition).getMediaPath());
        notifyDataSetChanged();
    }


    public long containsPic(Uri mUri){
        for(Medias media : mGallery){
            if(media.mediaPath == mUri)
                return media.deletionTime;
        }
        return -1;
    }

    public void setPicTime(Uri mUri, long deletionTime){
        for(Medias media : mGallery){
            if(media.mediaPath == mUri)
                media.setDeletionTime(deletionTime);
        }
    }

    public interface Callback{
        MediaAdapter getAdapter();
    }

}
