package edu.rosehulman.lewistd.photolifetime;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;

/**
 * Created by parks8 on 2018-04-30.
 */

public class PhotoLifeTime implements Parcelable{
    private String photoUri;
    private String timeStamp;
    private String key;

    public PhotoLifeTime(){}

    public PhotoLifeTime(String PhotoUri, String TimeStamp){
        this.photoUri=PhotoUri;
        this.timeStamp=TimeStamp;
    }

    protected PhotoLifeTime(Parcel in){
        this.photoUri = in.readString();
        this.timeStamp = in.readString();
    }

    public static final Creator<PhotoLifeTime> CREATOR = new Creator<PhotoLifeTime>() {
        @Override
        public PhotoLifeTime createFromParcel(Parcel in) {
            return new PhotoLifeTime(in);
        }

        @Override
        public PhotoLifeTime[] newArray(int size) {
            return new PhotoLifeTime[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(photoUri);
        dest.writeString(timeStamp);
    }

    public void setValues(PhotoLifeTime photoLifeTime){
        this.photoUri = photoLifeTime.getPhotoUri();
        this.timeStamp = photoLifeTime.getTimeStamp();

    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPhotoUri() {
        return photoUri;
    }
    public void setPhotoUri(String PhotoUri) {
        this.photoUri = PhotoUri;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String TimeStamp){
        this.timeStamp= TimeStamp;
    }

}
