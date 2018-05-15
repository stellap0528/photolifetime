package edu.rosehulman.lewistd.photolifetime;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder> {

    Context mContext;
    ArrayList<Medias> mGallery;
    RecyclerView mRecyclerView;
    Medias mCurrentMedia;
    String mUid;
    DatabaseReference mPhotoLifetimeRef;
    Query mQueryRef;
    FragmentManager mFM;

//    ArrayList<Medias> mMedias;

    public MediaAdapter(Context context, RecyclerView rView, FragmentManager fm) {
        mContext = context;
        mRecyclerView = rView;
        mGallery = new ArrayList<>();
        mFM = fm;
//        mUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mPhotoLifetimeRef = FirebaseDatabase.getInstance().getReference().child("weatherpics");
        mQueryRef = mPhotoLifetimeRef.orderByChild("uid").equalTo(mUid);
//        mGallery.mMedia.add(new Medias());
    }


    public MediaAdapter(Context context, RecyclerView rView, FragmentManager fm, ArrayList<Medias> newGallery) {
        mContext = context;
        mRecyclerView = rView;
        mGallery = new ArrayList<>();
        mFM = fm;
        mGallery = newGallery;
//        mGallery.mMedia.add(new Medias());
    }

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
        if (currentMedia.mediaPath != null) {
            //holder.mImageView.setImageResource(R.mipmap.ic_launcher_round);
            Bitmap myBitmap = BitmapFactory.decodeFile(mGallery.get(position).mediaPath);

            try {
                ExifInterface exif = new ExifInterface(mGallery.get(position).mediaPath);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                Log.d("EXIF", "Exif: " + orientation);
                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);
                }
                else if (orientation == 3) {
                    matrix.postRotate(180);
                }
                else if (orientation == 8) {
                    matrix.postRotate(270);
                }
                myBitmap = Bitmap.createBitmap(myBitmap, 100, 250, myBitmap.getWidth()/3, myBitmap.getHeight()/2, matrix, true); // rotating bitmap
            }
            catch (Exception e) {

            }
            holder.mImageView.setImageBitmap(myBitmap);
        }
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
                    ViewImageFrag mImageFragment = new ViewImageFrag().newInstance(mGallery.get(getAdapterPosition()).mediaPath);
                    FragmentTransaction ft = mFM.beginTransaction();
                    ft.replace(R.id.container_layout, mImageFragment);
                    ft.addToBackStack("image");
                    ft.commit();
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
                                    break;
                                case 1:
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
                                    String myFilePath = mGallery.get(getAdapterPosition()).mediaPath;
                                    File fileWithinMyDir = new File(myFilePath);

                                    if(fileWithinMyDir.exists()) {
                                        intentShareFile.setType("image/jpeg");
                                        intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+myFilePath));

                                        intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                                                "Sharing File...");
                                        intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");

                                        mContext.startActivity(Intent.createChooser(intentShareFile, "Share File"));
                                    }
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

    private void deleteMedia(int adapterPosition) {
        File file = new File(mGallery.get(adapterPosition).mediaPath);
        file.delete();

        //context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(photoPath))));

        mGallery.remove(adapterPosition);
        notifyDataSetChanged();
    }


    public long containsPic(Uri mUri){
        String path =  mUri.getPath();
        for(Medias media : mGallery){
            if(media.mediaPath == path)
                return media.deletionTime;
        }
        return 0;
    }
}
