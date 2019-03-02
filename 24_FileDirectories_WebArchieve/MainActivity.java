package com.aravindsankaran.webarchieveandsystemdirs;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        try {
            Log.i("Files dir Abs path" , getCacheDir().getAbsolutePath());
            //data/user/0/com.aravindsankaran.webarchieveandsystemdirs/files/
            Log.i("Files dir Canonical" , getFilesDir().getCanonicalPath());
            //data/data/com.aravindsankaran.webarchieveandsystemdirs/files
            Log.i("Cache dir" , getCacheDir().getCanonicalPath());
            //data/data/com.aravindsankaran.webarchieveandsystemdirs/cache
            Log.i("External Cache dir" , getExternalCacheDir().getCanonicalPath());
            //storage/emulated/0/Android/data/com.aravindsankaran.webarchieveandsystemdirs/cache

        } catch (Exception e) {
            e.printStackTrace();
        }


        WebView webView = (WebView) findViewById(R.id.webView);

        final File archieve = new File(getFilesDir(),"archieve.mht");
        Log.i("archive",archieve.getAbsolutePath());
        //data/user/0/com.aravindsankaran.webarchieveandsystemdirs/files/archieve.mht

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                //save archive after the webpage is loaded. Otherwise, we get empty page
                if(!archieve.exists()){
                    view.saveWebArchive(archieve.getAbsolutePath());
                    Log.i("saved","page saved");
                }
                super.onPageFinished(view, url);
            }
        });

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            webView.getSettings().setAllowFileAccessFromFileURLs(true);
            webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        }


        //load page if connected to internet or restore from archieve if available
        if(isConnected())
            webView.loadUrl("http://www.google.com");
        else{
            if(archieve.exists())
                webView.loadUrl("file://" + archieve.getAbsolutePath());
            else
                webView.loadData("<h1>No Connection</h1>","text/html","UTF-8");
        }

    }

    boolean isConnected(){
        ConnectivityManager cm =
                (ConnectivityManager)getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean connection = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return connection;
    }

}
