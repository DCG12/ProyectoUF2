package com.example.a46406163y.proyectouf2;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.bumptech.glide.Glide;


import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private Button bttnfoto;
    private Button bttnvideo;
    private Button bttnRec;
    private Button bttnMap;
    private Button bttnCall;
    private String mCurrentPhotoPath;
    private ImageView Vista;

    private MediaPlayer mp = new MediaPlayer();

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;

    private GPS gps;
    double longitude;
    double latitude;

    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int ACTIVITAT_SELECCIONAR_IMATGE = 1;
    private static final int REQUEST_TAKE_VIDEO = 2;
    private Uri fileUri;

    private POJO pojo;

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
        bttnCall =(Button) view.findViewById(R.id.bttncall);

        Vista = (ImageView) view.findViewById(R.id.imagen);

        gps = new GPS(getContext());


        if(gps.canGetLocation()){


            longitude = gps.getLongitude();
            latitude = gps .getLatitude();

            Log.d("DEBUG", String.valueOf(latitude));
        }
        else
        {

            gps.showSettingsAlert();
        }

        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference("lugares");



        super.onCreate(savedInstanceState);


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

        bttnMap.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),Mapa.class);
                startActivity(intent);
            }

        });

        bttnRec.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

        pojo = new POJO(mCurrentPhotoPath, longitude, latitude );

        DatabaseReference newReference = reference.push();
        newReference.setValue(pojo);
            }

        });

        bttnCall.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {


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
        /*
        DatabaseReference newReference = reference.push();
        newReference.setValue(image.getAbsolutePath(),latitude);*/

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

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        try {

            if (requestCode == REQUEST_TAKE_PHOTO) {

                Glide.with(getContext()).load( mCurrentPhotoPath).into(Vista);

                if(requestCode == REQUEST_TAKE_VIDEO){
                    
                    Glide.with(getContext()).load( mCurrentPhotoPath).into(Vista);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public static Bitmap getVideoFrame(Context context, Uri uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(uri.toString(),new HashMap<String, String>());
            return retriever.getFrameAtTime();
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
            }
        }
        return null;
    }

}