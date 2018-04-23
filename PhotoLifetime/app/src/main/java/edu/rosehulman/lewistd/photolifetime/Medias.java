package edu.rosehulman.lewistd.photolifetime;

import android.graphics.Bitmap;

import java.io.File;

public class Medias {

    File mMediaFile;
    Bitmap thumbnail;
    Bitmap fullImage;
    String mediaPath;
    int placehold;

    public Medias() {

        placehold = R.mipmap.ic_launcher_round;

    }

    public Medias(Bitmap thumbnail, String path) {
        this.thumbnail = thumbnail;
        this.mediaPath = path;
    }
}
