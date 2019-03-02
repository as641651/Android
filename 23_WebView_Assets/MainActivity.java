package com.aravindsankaran.webview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {

    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = (WebView) findViewById(R.id.webView);

        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient());

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

        //load external URL
        //webView.loadUrl("http://www.google.com");


        //we can also load local HTML content
        webView.loadUrl("file:///android_asset/reaction/proj.html");

    }

    //To make the back key to navigate back the webpage
    //otherwise, the back key will directly close the app
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
