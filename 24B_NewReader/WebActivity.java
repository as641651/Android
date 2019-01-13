package com.aravindsankaran.newsreader;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.File;

public class WebActivity extends AppCompatActivity {

    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        webView = (WebView) findViewById(R.id.webView);

        Intent intent = getIntent();
        int position = intent.getIntExtra("position",-1);
        String id = intent.getStringExtra("id");

        final File archieve = new File(getFilesDir(),id + ".mht");

        if(position!=-1){
            webView.setWebViewClient(new WebViewClient(){
                @Override
                public void onPageFinished(WebView view, String url) {
                    if(!archieve.exists()){
                        view.saveWebArchive(archieve.getAbsolutePath());
                        Log.i("Saved", "Saved web page as archieve");
                    }
                    super.onPageFinished(view, url);
                }
            });
            setWebViewSetting();
            if(!archieve.exists())
                webView.loadUrl(MainActivity.articleUrls.get(position));
            else{
                webView.loadUrl("file://" + archieve.getAbsolutePath());
                Log.i("Using archieve", "Loaded archieve");
                Toast.makeText(getApplicationContext(),"Loaded from archieve",Toast.LENGTH_LONG).show();
            }
        }
    }

    void setWebViewSetting(){
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            webView.getSettings().setAllowFileAccessFromFileURLs(true);
            webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        }

        //make the webpage fit the device screen. Needed if bootstrap was not used
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        //enable zoom
        webView.getSettings().setBuiltInZoomControls(true);
        //otherwise you ll have a zoom button showing over the layout
        webView.getSettings().setDisplayZoomControls(false);
    }

    //To make the back key to navigate back the webpage
    //otherwise, the back key will directly close the app
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (webView.canGoBack()) {
                    webView.goBack();
                    return true;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
