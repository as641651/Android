package com.aravindsankaran.httpfileupload;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ask permission to read external storage
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }else{
            Log.i("Permission", "Already available");
            upload_image();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i("Permission","Asking");
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){
            upload_image();
        }else{
            Log.i("Permission", "Denied by user");
        }
    }

    public void upload_image(){

        Log.i("upload task","started");

        String sourceFileUri = "/mnt/sdcard/Pictures/animate2.jpeg";
        Map<String,String> requestData = new HashMap<>();
        requestData.put("uri", sourceFileUri);
        requestData.put("url","http://192.168.1.2:8000/api/recipe/recipes/6/upload-image/");
        requestData.put("credential","9076b9588a52f15ef40198d164aa5b3855daf5e5");

        UploadFileRequest uploadFileRequest = new UploadFileRequest();
        try {
            Map<String,String> result = uploadFileRequest.execute(requestData).get();
            Log.i("upload task","done");
            if(result!=null){
                Log.i("status code", result.get("status code"));
                Log.i("status msg", result.get("status msg"));
            }

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public class UploadFileRequest extends AsyncTask<Map<String,String>, Void, Map<String,String>>{

        @Override
        protected Map<String, String> doInBackground(Map<String, String>... maps) {
            Map<String,String> result = new HashMap<>();

            File sourceFile = new File(maps[0].get("uri"));

            if(!sourceFile.isFile()){
                Log.i("file", "not found");
                return null;
            }

            try {

                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";
                int bytesRead, bytesAvailable, bufferSize;
                byte[] buffer;
                int maxBufferSize = 1*1024*1024; //1MB


                URL url = new URL(maps[0].get("url"));
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestProperty("Authorization", "Token "+maps[0].get("credential"));
                conn.setRequestProperty("Connection","Keep-Alive");
                conn.setRequestProperty("ENCTYPE","multipart/form-data");
                conn.setRequestProperty("Content-Type","multipart/form-data;boundary="+boundary);
                // field name in server
                conn.setRequestProperty("image",maps[0].get("uri"));
                conn.setRequestMethod("POST");
                conn.setUseCaches(false);

                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens+boundary+lineEnd);
                dos.writeBytes(
                        "Content-Disposition: form-data; name=\"image\";filename=\""
                        + maps[0].get("uri") + "\"" + lineEnd);
                dos.writeBytes(lineEnd);

                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                //create buffer size based on availability
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable,maxBufferSize);
                buffer = new byte[bufferSize];

                //read file : buffer, offset, buffersize
                bytesRead = fileInputStream.read(buffer,0,bufferSize);
                while(bytesRead>0){
                    dos.write(buffer,0,bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable,maxBufferSize);
                    bytesRead = fileInputStream.read(buffer,0,bufferSize);
                }

                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens+boundary+twoHyphens+lineEnd);

                int status_code = conn.getResponseCode();
                result.put("status code",String.valueOf(status_code));
                result.put("status msg", conn.getResponseMessage());

                fileInputStream.close();
                dos.flush();
                dos.close();

                return result;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }
    }
}


