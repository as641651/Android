package com.aravindsankaran.images;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    boolean changed = false;
    public void clickFunction(View view){
        ImageView image = (ImageView) findViewById(R.id.imageView);
        if(!changed) {
            image.setImageResource(R.drawable.cat2);
            changed = true;
        }else{
            image.setImageResource(R.drawable.cat1);
            changed = false;
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
