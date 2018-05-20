package com.tutorial.camera_base64;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tutorial.camera_base64.resources.retrofit.Loader;
import com.tutorial.camera_base64.resources.retrofit.SyncOnline;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Uri imageUri;
    static int CAMERA_PIC_REQUEST = 1;
    String ImagePathtoUpload;
    Activity act;

    TextView Base64ResultTxt;
    Button openCameraBtn, sendDataOnlineBtn;
    SyncOnline sync;
    Loader ldr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        listeners();

    }

    private void initUI(){

        act = MainActivity.this;
        sync = new SyncOnline( act );
        ldr = new Loader();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions( act,
                    new String[]{ Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 1);
        }

        Base64ResultTxt = findViewById( R.id.Base64ResultTxt );

        openCameraBtn = findViewById( R.id.openCameraBtn );
        sendDataOnlineBtn = findViewById( R.id.sendDataOnlineBtn );

    }

    private void listeners(){

        openCameraBtn.setOnClickListener( this );
        sendDataOnlineBtn.setOnClickListener( this );

    }


    @Override
    public void onClick(View view) {

        switch ( view.getId() ){

            case R.id.openCameraBtn:{
                TakePicture();
            }
            break;

            case R.id.sendDataOnlineBtn:{

                if( Base64ResultTxt.getText().toString().trim().isEmpty() ){

                    Toast.makeText(act, "Picture not found", Toast.LENGTH_SHORT).show();

                }else{

                    if( sync.networkAvailable() ){
                        sync.sendDataOnline( act, ldr, ImagePathtoUpload, Base64ResultTxt  );
                    }else {
                        sync.CustomDialogNoInternet();
                    }


                }

            }
            break;

        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {


                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    Toast.makeText(getApplicationContext(), "Permissions Granted", Toast.LENGTH_SHORT).show();


                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {

                    Toast.makeText(getApplicationContext(), "Permissions are required", Toast.LENGTH_SHORT).show();

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }

            }
            break;

        }
    }





    public void CameraDialog(final Activity ctx, Uri selectedImage, Bitmap bmp, final String ImagePathtoUpload){

        final Dialog dialog = new Dialog( ctx );

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.camera_frag);

        DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.80);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT); //set below the setContentview

        ImageView CameraImg = (ImageView) dialog.findViewById(R.id.CameraImg);
        Button CaptureBtn = (Button) dialog.findViewById(R.id.CaptureBtn);
        Button ProceedBtn = (Button) dialog.findViewById(R.id.ProceedBtn);

/*
        Picasso.with( ctx ).load(bmp).resize(200,200).centerCrop()
                .placeholder(R.mipmap.ic_launcher).into(CameraImg);
*/

        CameraImg.setImageBitmap(bmp);

        ProceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();

            }
        });

        CaptureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

                TakePicture();

            }
        });




        dialog.show();


    }



    private void TakePicture() {

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        File photo = new File(Environment.getExternalStorageDirectory(),  "Pic.jpg");

        imageUri = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri= FileProvider.getUriForFile( act, BuildConfig.APPLICATION_ID+".provider", photo);
        } else {
            imageUri = Uri.fromFile(photo);
        }

        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri );

        startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);


    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_PIC_REQUEST  && resultCode == Activity.RESULT_OK ) {

            //Uri selectedImage = imageUri;
            act.getContentResolver().notifyChange(imageUri, null);
            ContentResolver cr = act.getContentResolver();
            Bitmap bitmap;
            try {
                bitmap = android.provider.MediaStore.Images.Media
                        .getBitmap(cr, imageUri);

                Bitmap convertedImg = getResizedBitmap( bitmap, 400 );

                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                convertedImg.compress(Bitmap.CompressFormat.JPEG, 90, bao);
                byte[] ba = bao.toByteArray();
                ImagePathtoUpload = Base64.encodeToString(ba, Base64.NO_WRAP);
               /* Toast.makeText(act, selectedImage.toString(),
                        Toast.LENGTH_LONG).show();*/

                Log.d("BASE64", ImagePathtoUpload+"--");

                Base64ResultTxt.setText( ImagePathtoUpload );

                Thread.sleep(1000);

                CameraDialog( act, imageUri, bitmap, ImagePathtoUpload );


            } catch (Exception e) {
                Base64ResultTxt.setText( "Failed to develop image, error:\n\n"+e.toString() );

                Toast.makeText(act, "Failed to develop image", Toast.LENGTH_SHORT)
                        .show();
                e.printStackTrace();

            }
        }
    }


    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }









}
