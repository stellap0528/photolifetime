package edu.rosehulman.lewistd.photolifetime;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageView;

import edu.rosehulman.lewistd.photolifetime.Fragments.ImageListFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder> {

    int mYear,mMonth,mDate;
    Context mContext;
    ArrayList<Medias> mGallery;
    private ChildEventListener mChildEventListener;

    Medias mCurrentMedia;
    String mUid;

    private Calendar deletionCalendar = Calendar.getInstance();
    private Calendar warningCalendar = Calendar.getInstance();
    DatabaseReference mPhotoLifetimeRef;
    Query mQueryRef;
    LayoutInflater mInflater;
    Activity mActivity;
    ImageListFragment.Callback mCallback;

    public MediaAdapter(Activity activity, ImageListFragment.Callback callback, Context context) {

        Log.d("bilada", "in adapter");
        mActivity = activity;
        mContext = context;
        mCallback = callback;
        mGallery = new ArrayList<>();
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mPhotoLifetimeRef = FirebaseDatabase.getInstance().getReference().child("Medias");
        mChildEventListener = new MediaEventListener();
        mQueryRef = mPhotoLifetimeRef.orderByChild("uid").equalTo(mUid);
        mQueryRef.addChildEventListener(mChildEventListener);
    }

    public String getUid() {
        return this.mUid;
    }

    class MediaEventListener implements ChildEventListener{

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Medias medias = dataSnapshot.getValue(Medias.class);
            medias.setKey(dataSnapshot.getKey());
            mGallery.add(medias);
            notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            String key = dataSnapshot.getKey();
            Medias newMedias = dataSnapshot.getValue(Medias.class);
            for(Medias medias : mGallery){
                if(medias.getKey().equals(key)){
                    medias.setValues(newMedias);
                    break;
                }
            }
            notifyDataSetChanged();
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            String key = dataSnapshot.getKey();
            for(Medias medias : mGallery){
                if(key.equals(medias.getKey())){
                    mGallery.remove(medias);
                    notifyDataSetChanged();
                    break;
                }
            }
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
        final Medias currentMedia = mGallery.get(position);
        holder.mImageView.setImageURI(Uri.parse(mGallery.get(position).mediaPath));
        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onDocSelected(Uri.parse(currentMedia.getMediaPath()));
//
            }
        });
        holder.mImageView.setOnLongClickListener(new onLong(currentMedia));
    }

    @Override
    public int getItemCount() {
        return mGallery.size();
    }

    public void addPic(Medias picture) {
        mPhotoLifetimeRef.push().setValue(picture);
//        mGallery.add(picture);
//        notifyDataSetChanged();
    }


    public class onLong implements View.OnLongClickListener{

        Medias mMedia;
        onLong(Medias currentMedia){
            mMedia = currentMedia;
        }
        @Override
        public boolean onLongClick(final View v) {
            mCurrentMedia = mMedia;
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(R.string.dialog_title);
            builder.setItems(R.array.dialog_array, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            showPhotoLifeTime(Uri.parse(mCurrentMedia.getMediaPath()));
                            break;
                        case 1:
                            addEditPhotoLifeTime(Uri.parse(mCurrentMedia.getMediaPath()));
                            break;
                        case 2:
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setTitle(R.string.safety_delete_dialog);
                            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteMedia(Uri.parse(mMedia.getMediaPath()));
                                    Snackbar.make(v, "Item Deleted", Snackbar.LENGTH_SHORT)
                                            .show();
                                }
                            });
                            builder.setNegativeButton(android.R.string.no, null);
                            builder.create().show();
                            break;
                        case 3:
                            Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                            Uri myFilePath = Uri.parse(mMedia.mediaPath);


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
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;

        public ViewHolder(final View itemView) {
            super(itemView);
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mCallback.onDocSelected(mGallery.get(getAdapterPosition()).getMediaPath());
////
//                }
//            });
//            itemView.setOnLongClickListener(new onLong());
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
                deletionCalendar.set(mYear, mMonth, mDate, 11, 7, 0);
                Date date = new Date();

                Log.d("check", "date set to :"+deletionCalendar.getTimeInMillis()+" current time: "+ deliveryDateView.getDate()+"  "+deletionCalendar.getTimeInMillis());
                Log.d("mili:   ", deletionCalendar.getTimeInMillis()-warningCalendar.getTimeInMillis()+"");
                ((MainActivity) mActivity).setIntentArray(warningCalendar.getTimeInMillis(), deletionCalendar.getTimeInMillis(), mCurrentMedia);

                    setPicTime(mCurrentMedia, deletionCalendar.getTimeInMillis());
            }
        });
        builder.setNeutralButton("delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((MainActivity) mActivity).deleteAlarm(mUri);
                setPicTime(mCurrentMedia, -1);
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

    public void deleteMedia(Uri mUri) {
        Medias delete = null;
        for(Medias media: mGallery){

            Log.d("compare", media.getMediaPath()+"    uri:" +mUri.toString());
            if(media.getMediaPath().equals(mUri.toString())){
                delete = media;
                break;
            }
        }
        mPhotoLifetimeRef.child(delete.getKey()).removeValue();

        ((MainActivity)mActivity).deleteAlarm(Uri.parse(delete.getMediaPath()));
    }



    public long containsPic(Uri mUri){
        for(Medias media : mGallery){
            if(media.mediaPath == mUri.toString())
                return media.deletionTime;
        }
        return -1;
    }

    public void setPicTime(Medias media, long deletionTime){

        media.setDeletionTime(deletionTime);
        mPhotoLifetimeRef.child(media.getKey()).setValue(media);
        ((MainActivity)mActivity).setIntentArray(deletionTime-126420000,deletionTime, media);
//        for(Medias media : mGallery){
//            if(media.mediaPath == mUri.toString())
//                media.setDeletionTime(deletionTime);
//        }
    }

}
