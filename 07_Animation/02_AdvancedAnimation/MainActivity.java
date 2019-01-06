package com.aravindsankaran.a07_animationadvanced;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    public void animateFunction(View view){
        ImageView bart = (ImageView) findViewById(R.id.imageView);
        bart.animate().translationXBy(1000f).translationYBy(1000f).rotation(3600f).setDuration(2000);

        //traslate with respect to absolute coords
        //bart.animate().translationX(180f).translationY(140f).rotation(3600f).setDuration(2000);

        //feels like widget is moving in z axis
        //bart.animate().scaleX(0.5f).scaleY(0.5f).translationXBy(200f).translationYBy(300f).setDuration(2000);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView bart = (ImageView) findViewById(R.id.imageView);

        //set the image initially out of screen
        bart.setTranslationX(-1000f);
        bart.setTranslationY(-1000f);

    }
}
