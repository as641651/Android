package com.aravindsankaran.takephotos;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Environment.DIRECTORY_PICTURES;
import static android.os.Environment.getExternalStoragePublicDirectory;

public class MainActivity extends AppCompatActivity {

    public void onCapture(View view){
        Log.i("button","Capture clicked");


        //dispatchTakePictureIntent();
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){
            dispatchTakePictureSaveIntent();
        }else{
            Log.i("permission", "denied");
        }
    }


    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_FROM_DIR = 2;
    ImageView mImageView;
    String mImagePath;
    Uri photoURI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = (ImageView) findViewById(R.id.imageView);

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        }
    }


    //capture a photo by delegating the work to another camera app on the device.
    // however, this app cannot access the full image. only a thumbnail can be obtained
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    // To save the picture, create a FileProvider and specify the path in an xml file
    private void dispatchTakePictureSaveIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            photoFile = createImageFile();
            if(photoFile != null){
                // get uri from FileProvider
                // if URI is obtained as URI.fromFile, you will get URIExposed exception
                photoURI = FileProvider.getUriForFile(this,"com.aravindsankaran.fileprovider",photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_FROM_DIR);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // can obtain only thumbnail
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(imageBitmap);

        }
        if (requestCode == REQUEST_IMAGE_FROM_DIR && resultCode == RESULT_OK){
            mImageView.setImageURI(photoURI);

        }
    }

    private File createImageFile(){
        // create time stamp to avoid collision
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timestamp;
        // pic is public. can be accessed through gallery under the folder Pictures
        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        ///storage/emulated/0/Pictures
        // pic is private to app. cannot access via gallery
        //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        // storage/emulated/0/Android/data/com.aravindsankaran.takephotos/files/Pictures
        Log.i("dir", storageDir.getAbsolutePath());


        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        mImagePath = image.getAbsolutePath();

        return image;

    }
}
