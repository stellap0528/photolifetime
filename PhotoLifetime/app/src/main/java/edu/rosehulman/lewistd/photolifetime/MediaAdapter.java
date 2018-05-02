package edu.rosehulman.lewistd.photolifetime;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder> {

    Context mContext;
    Gallery mGallery;
    RecyclerView mRecyclerView;
    Medias mCurrentMedia;
//    ArrayList<Medias> mMedias;

    public MediaAdapter(Context context, RecyclerView rView) {
        mContext = context;
        mRecyclerView = rView;
        mGallery = new Gallery();
//        mGallery.mMedia.add(new Medias());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.media_view, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
        Medias currentMedia = mGallery.mMedia.get(position);
        if (currentMedia.placehold == R.mipmap.ic_launcher_round) {
            //holder.mImageView.setImageResource(R.mipmap.ic_launcher_round);
        } else {

            Bitmap myBitmap = BitmapFactory.decodeFile(mGallery.mMedia.get(position).mediaPath);

            try {
                ExifInterface exif = new ExifInterface(mGallery.mMedia.get(position).mediaPath);
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
                myBitmap = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), matrix, true); // rotating bitmap
            }
            catch (Exception e) {

            }
//            myBitmap = Bitmap.createScaledBitmap(myBitmap, 50, 100, true);
            holder.mImageView.setImageBitmap(myBitmap);
//            holder.mImageView.setImageBitmap(mGallery.mMedia.get(position).thumbnail);
        }
    }

    @Override
    public int getItemCount() {
        return mGallery.mMedia.size();
    }

    public void addPic(Medias picture) {
        mGallery.mMedia.add(picture);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;

        public ViewHolder(final View itemView) {
            super(itemView);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mCurrentMedia = mGallery.mMedia.get(getAdapterPosition());
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
        File file = new File(mGallery.mMedia.get(adapterPosition).mediaPath);
        file.delete();

        //context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(photoPath))));

        mGallery.mMedia.remove(adapterPosition);
        notifyDataSetChanged();
    }
}
