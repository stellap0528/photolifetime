package edu.rosehulman.lewistd.photolifetime.Fragments;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
        args.putString(ARG_PATH, mPath.toString());
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
        iView.setImageURI(mPath);
        return imageView;
    }

}
