package com.example.a46406163y.proyectouf2;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {


    private Button bttnfoto;
    private Button bttnvideo;
    private Button bttnRec;
    private Button bttnMap;
    private String mCurrentPhotoPath;
    private GridView Vista;

    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int ACTIVITAT_SELECCIONAR_IMATGE = 1;
    private static final int REQUEST_TAKE_VIDEO = 2;
    private Uri fileUri;;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        bttnfoto =(Button) view.findViewById(R.id.bttnfoto);
        bttnvideo =(Button) view.findViewById(R.id.bttnvideo);
        bttnMap =(Button) view.findViewById(R.id.bttnmapa);
        bttnRec =(Button) view.findViewById(R.id.bttnrep);

        bttnfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dispatchTakePictureIntent();

            }
        });

        bttnvideo.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View v){

                dispatchTakeVideoIntent();

            }
        });

        return view;
    }
    
    private File createImageFile(int requestTakePhoto)  {

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,
                    ".jpg",
                    storageDir
            );
        } catch (IOException e) {
            e.printStackTrace();
        }


        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();

        return image;
    }

    private File createVideoFile(int requestTakeVideo)  {

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String videoFileName = "MP4_" + timeStamp + "_";

        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        File video = null;
        try {
            video = File.createTempFile(
                    videoFileName,
                    ".mp4",
                    storageDir
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + video.getAbsolutePath();

        return video;
    }

    private void dispatchTakePictureIntent() {


        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {


            File photoFile = null;
            photoFile = createImageFile(REQUEST_TAKE_PHOTO);

            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

            }
        }
    }

    private void dispatchTakeVideoIntent() {


        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        if (takeVideoIntent.resolveActivity(getContext().getPackageManager()) != null) {


            File videoFile = null;
            videoFile = createVideoFile(REQUEST_TAKE_VIDEO);

            if (videoFile != null) {
                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(videoFile));
                startActivityForResult(takeVideoIntent, REQUEST_TAKE_VIDEO);

            }
        }
    }
}