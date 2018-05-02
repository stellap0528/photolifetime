package edu.rosehulman.lewistd.photolifetime;

import android.graphics.Bitmap;

import java.io.File;

public class Medias {

    File mMediaFile;
    String mediaPath;
    int placehold;

    public Medias() {

        placehold = R.mipmap.ic_launcher_round;

    }

    public Medias(String path) {
        this.mediaPath = path;
    }
}
