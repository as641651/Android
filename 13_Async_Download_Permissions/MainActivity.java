package com.aravindsankaran.download;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    //Running a task in background thread

    //AsyncTask is used when we want to run this part of the code in a different thread from main thread!
    //AsyncTask<Dtype sent to DownloadTask class, Dtype of method that shows progress, Dtype returned by DownloadTask class
    public class DownloadTask extends AsyncTask<String, Void, String> {

        //this method takes a variable length argument. Ie, can pass arbitary no of args
        @Override
        protected String doInBackground(String... urls) {
            Log.i("URLs" , urls[0]);
            Log.i("URLs" , urls[1]);

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            //Mandatory try catch
            try {
                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection)url.openConnection();

                InputStream in = urlConnection.getInputStream();

                //we need a reader to read input stream,
                InputStreamReader reader = new InputStreamReader(in);

                //reads one char at a time
                int data = reader.read();

                while(data!=-1){
                    char current = (char)data;
                    result += current;
                    data = reader.read();
                }

            } catch (java.io.IOException e) {
                e.printStackTrace();
            }

            return result;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DownloadTask task = new DownloadTask();

        try {
            //mandatory to surround with try and catch when calling async tasl!
            String result = task.execute("https://www.ecowebhosting.co.uk","https://www.aravindsankaran.me").get();
            Log.i("Contents of URL",result);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
