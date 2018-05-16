package edu.rosehulman.lewistd.photolifetime;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

public class Medias implements Parcelable{


    String mediaPath;
    long deletionTime;
    String uid;
    String key;

    public Medias() {


    }

    public Medias(String path, long deletionTime, String uid) {
        this.mediaPath = path;
        this.deletionTime = deletionTime;
        this.uid = uid;
    }

    protected Medias(Parcel in){
        mediaPath = in.readParcelable(Medias.class.getClassLoader());
        deletionTime = in.readLong();
    }

    public static final Parcelable.Creator<Medias> CREATOR = new Parcelable.Creator<Medias>() {
        @Override
        public Medias createFromParcel(Parcel in) {
            return new Medias(in);
        }

        @Override
        public Medias[] newArray(int size) {
            return new Medias[size];
        }
    };

    public String getMediaPath(){return mediaPath;}

    public String getUid(){return this.uid;}

    public long getDeletionTime(){return this.deletionTime;}

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValues(Medias media){
        this.mediaPath = media.getMediaPath();
        this.deletionTime = media.getDeletionTime();
    }

    public void setMediaPath(String mediaPath){
        this.mediaPath = mediaPath;
    }

    public void setUid(String Uid){
        this.uid = Uid;
    }

    public void setDeletionTime(long deletionTime){
        this.deletionTime = deletionTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mediaPath);
        dest.writeLong(deletionTime);
    }


}
