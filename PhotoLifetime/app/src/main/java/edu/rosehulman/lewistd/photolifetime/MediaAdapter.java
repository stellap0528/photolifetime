package edu.rosehulman.lewistd.photolifetime;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder> {

    Context mContext;
    Gallery mGallery;
    RecyclerView mRecyclerView;
//    ArrayList<Medias> mMedias;

    public MediaAdapter(Context context, RecyclerView rView) {
        mContext = context;
        mRecyclerView = rView;
        mGallery = new Gallery();
        mGallery.mMedia.add(new Medias());
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
            holder.mImageView.setImageResource(R.mipmap.ic_launcher_round);
        } else {
            holder.mImageView.setImageBitmap(mGallery.mMedia.get(position).thumbnail);
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

        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }
}
