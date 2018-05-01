package edu.rosehulman.lewistd.photolifetime;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.net.URISyntaxException;
import java.util.zip.Inflater;

/**
 * Created by parks8 on 2018-04-30.
 */

public class gallaryPicFragment extends Fragment {

    private Uri mUri;
    private ImageView mGalleryPicView;

    public gallaryPicFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
        Log.d("gallaryFragment", "Fragment opend");
        View rootView = inflater.inflate(R.layout.gallary_pic, container, false);
        mGalleryPicView = rootView.findViewById(R.id.galaray_image_view);
        mUri = getArguments().getParcelable(MainActivity.ARG_GALLARYPIC);
        mGalleryPicView.setImageDrawable(null);
        mGalleryPicView.setImageURI(mUri);

        setHasOptionsMenu(true);
        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_edit, menu);
        return;
    }




}
