package com.aravindsankaran.httprest;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.loadUrl("http://192.168.1.2:8000/admin");

        call_with_GET();
        //call_with_POST();

    }

    public void call_with_GET(){

        // API from django server
        // get IP address of PC using 'ifconfig' command
        String api = "http://192.168.1.2:8000/api/recipe/recipes/";

        // payload has to be encoded in the URL
        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("ingredients", "4")
                .appendQueryParameter("tags", "6");
        String query = builder.build().getEncodedQuery();

        api = api + "?" + query;
        //http://192.168.1.2:8000/api/recipe/recipes/?ingredients=4&tags=6
        Log.i("Url call", api);

        //Send the url and credentials to be added to the header (if auth needed)
        Map<String,String> req_data = new HashMap<>();
        req_data.put("url",api);
        req_data.put("credential","9076b9588a52f15ef40198d164aa5b3855daf5e5");

        SendGetRequest getRequest = new SendGetRequest();

        try {
            Map<String,String> result = getRequest.execute(req_data).get();
            if(result!=null){
                Log.i("Status msg", result.get("status msg"));
                Log.i("Code", result.get("status code"));
                Log.i("Response msg", result.get("response msg"));
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public class SendGetRequest extends AsyncTask<Map<String,String>, Void, Map<String,String>>{

        @Override
        protected Map<String,String> doInBackground(Map<String,String>... maps) {
            Map result = new HashMap();
            try {

                URL url = new URL(maps[0].get("url"));
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                //add credentials to header
                conn.setRequestProperty("Authorization", "Token "+maps[0].get("credential"));
                conn.setRequestMethod("GET");

                conn.connect();
                result.put("status msg",conn.getResponseMessage());
                result.put("status code",String.valueOf(conn.getResponseCode()));

                int responseCode = conn.getResponseCode();
                String response_msg = "";
                if(responseCode==HttpURLConnection.HTTP_OK){
                    InputStream in = conn.getInputStream();
                    InputStreamReader reader = new InputStreamReader(in);
                    int data = reader.read();

                    while(data != -1){
                        char current = (char)data;
                        response_msg+=current;
                        data = reader.read();
                    }

                }
                result.put("response msg",response_msg);
                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Map<String,String> s) {
            super.onPostExecute(s);

        }
    }

    public void call_with_POST() {

        String api = "http://192.168.1.2:8000/api/user/token/";
        Map<String,String> url = new HashMap();
        url.put("url",api);

        Map<String,String> params = new HashMap<>();
        params.put("email","one@two.com");
        params.put("password","12345");

        SendPostRequest postRequest = new SendPostRequest();
        Map<String,String> result = null;
        try {
            result = postRequest.execute(url,params).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(result!=null){
            Log.i("Status msg", result.get("status msg"));
            Log.i("Code", result.get("status code"));
            Log.i("Response msg", result.get("response msg"));
        }


    }

    public class SendPostRequest extends AsyncTask<Map<String,String>, Void, Map<String,String>>{

        public String encodeParams(Map<String, String> params) {
            Uri.Builder builder = new Uri.Builder();

            for(Map.Entry<String,String> entry : params.entrySet()){
                builder.appendQueryParameter(entry.getKey(), entry.getValue());
            }

            return builder.build().getEncodedQuery();
        }

        @Override
        protected Map<String,String> doInBackground(Map<String, String>... maps) {
            Map<String,String> result = new HashMap();
            try {
                URL url = new URL(maps[0].get("url"));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os,"UTF-8")
                );
                writer.write(encodeParams(maps[1]));
                writer.flush();
                writer.close();
                os.close();

                conn.connect();
                result.put("status msg",conn.getResponseMessage());
                result.put("status code",String.valueOf(conn.getResponseCode()));

                String response_msg = "";
                if(conn.getResponseCode()==HttpURLConnection.HTTP_OK){
                    InputStream in = conn.getInputStream();
                    InputStreamReader reader = new InputStreamReader(in);
                    int data = reader.read();
                    while(data!=-1){
                        char current = (char)data;
                        response_msg+=current;
                        data = reader.read();
                    }

                }
                result.put("response msg",response_msg);

                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Map map) {
            super.onPostExecute(map);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(event.getAction()==KeyEvent.ACTION_DOWN){
            if(keyCode==KeyEvent.KEYCODE_BACK){
                if(webView.canGoBack())
                    webView.goBack();
                else
                    finish();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
