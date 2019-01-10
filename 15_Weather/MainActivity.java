package com.aravindsankaran.weather;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    public void findWeather(View view){
        EditText city = (EditText) findViewById(R.id.editText);

        String cityStr = city.getText().toString();

        //encode city name to deal with spaces (eg san francisco)
        try {
            cityStr = URLEncoder.encode(cityStr,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.i("City",cityStr);

        String apicall = "http://api.openweathermap.org/data/2.5/weather?q=" + cityStr + "&appid=4c46abf2ea93cc2a53347e603fdc1f0a";

        //This has to be instantiated inside because we can call the execute method only once per object
        DownloadTask getWeatherTask = new DownloadTask();

        //can be called only once per instance
        getWeatherTask.execute(apicall);

        //close the android keyboard
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(city.getWindowToken(),0);
    }

    public class DownloadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {

            URL url;
            HttpURLConnection connection = null;
            String result = "";

            try {
                url = new URL(strings[0]);
                connection = (HttpURLConnection)url.openConnection();

                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while(data != -1){
                    char current = (char)data;
                    result+=current;
                    data = reader.read();
                }

                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            String postStr = "";
            if(result!=null) {
                //if data is found
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray arr = jsonObject.getJSONArray("weather");

                    for (int i = 0; i < arr.length(); i++) {
                        String mainStr = arr.getJSONObject(i).getString("main");
                        String descrpt = arr.getJSONObject(i).getString("description");

                        postStr += mainStr + " : " + descrpt + '\n';

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                //if 404
                postStr = "City not found!";
            }
            TextView info = (TextView) findViewById(R.id.textView2);
            info.setText(postStr);
            Log.i("post",postStr);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }
}
