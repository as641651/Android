package com.aravindsankaran.animationfade;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    public void fadeFunction1(View view){
        Log.i("Info","Clicked image!");

        //any local variable in a method that needs to accessed by inner classes must be final.
        // if you dont want to declare final, make the variable global
        final ImageView img1 = (ImageView) findViewById(R.id.imageView2);
        ImageView img2 = (ImageView) findViewById(R.id.imageView3);

        img2.setVisibility(View.VISIBLE);

        img1.animate().alpha(0f).setDuration(2000).withEndAction(new Runnable() {
            @Override
            public void run() {
                //part of the code that will be executed after animation ends
                img1.setVisibility(View.GONE);
            }
        });

        img2.animate().alpha(1f).setDuration(2000);


    }

    public void fadeFunction2(View view){
        Log.i("Info","Clicked image!");

        ImageView img1 = (ImageView) findViewById(R.id.imageView2);
        final ImageView img2 = (ImageView) findViewById(R.id.imageView3);

        img1.setVisibility(View.VISIBLE);

        img1.animate().alpha(1f).setDuration(2000);
        img2.animate().alpha(0f).setDuration(2000).withEndAction(new Runnable() {
            @Override
            public void run() {
                img2.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
