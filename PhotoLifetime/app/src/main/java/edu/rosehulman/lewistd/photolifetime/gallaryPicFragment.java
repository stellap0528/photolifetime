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

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
        Log.d("gallaryFragment", "Fragment opend");
        View rootView = inflater.inflate(R.layout.gallary_pic, container, false);
        mGalleryPicView = rootView.findViewById(R.id.galaray_image_view);
        mUri = getArguments().getParcelable(MainActivity.ARG_GALLARYPIC);
        mGalleryPicView.setImageURI(null);
        mGalleryPicView.setImageURI(mUri);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_edit, menu);
        return;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.action_view_lifetime:
                return true;
            case R.id.action_edit_lifetime:
                return true;
            case R.id.action_delete:
                showDeleteDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void showDeleteDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle(R.string.delete);
        builder.setMessage(R.string.delete_message);
        builder.setPositiveButton(R.string.delete,  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    deleteImage();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
    }


    public void deleteImage() throws URISyntaxException {

        Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Images.Media.DATA + " = ?";
        ContentResolver contentResolver = getActivity().getContentResolver();
        String[] projection = { MediaStore.Images.Media._ID };
        String[] selectionArgs = new String[] {PathUtil.getPath(getContext(), mUri)};
        Cursor c = contentResolver.query(queryUri, projection, selection, selectionArgs, null);
        if(c.moveToFirst()){
            long id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
            Uri deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            contentResolver.delete(deleteUri, null, null);
        }
        c.close();
    }



}
