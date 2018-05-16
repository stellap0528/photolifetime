package edu.rosehulman.lewistd.photolifetime.Fragments;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.net.URISyntaxException;

import edu.rosehulman.lewistd.photolifetime.PathUtil;
import edu.rosehulman.lewistd.photolifetime.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewImageFrag extends Fragment {
    private static final String ARG_PATH = "URI";
    private Uri mPath;

    public ViewImageFrag() {
        // Required empty public constructor
    }

    public static ViewImageFrag newInstance(Uri mPath) {
        ViewImageFrag frag = new ViewImageFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PATH, mPath.getPath());
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPath = getArguments().getParcelable(ARG_PATH);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View imageView = inflater.inflate(R.layout.fragment_view_image, container, false);
        ImageView iView = imageView.findViewById(R.id.imageView2);
        Bitmap aBitmap;

            String filepath = mPath.getPath();
            aBitmap = BitmapFactory.decodeFile(filepath);

        try {
            ExifInterface exif = new ExifInterface(mPath.getPath());
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
            aBitmap = Bitmap.createBitmap(aBitmap, 0, 0, aBitmap.getWidth(), aBitmap.getHeight(), matrix, true); // rotating bitmap
        }
        catch (Exception e) {

        }
//        iView.setImageURI(mPath);
        iView.setImageBitmap(aBitmap);
//        iView.setImageURI(mPath);
        return imageView;
    }

}
