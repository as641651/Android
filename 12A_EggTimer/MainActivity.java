package com.aravindsankaran.eggtimer;

import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    CountDownTimer timer;
    SeekBar timeBar;
    boolean timerActive = false;

    public void timerFunction(View view){

        int timeout = timeBar.getProgress()*1000;
        final Button bview = (Button) view;

        if(!timerActive) {
            // if timer is not active, start countdown
            timerActive = true;
            timer = new CountDownTimer(timeout, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timeBar.setProgress((int) millisUntilFinished / 1000);
                }

                @Override
                public void onFinish() {

                    //deactivate timer to enable dragging
                    timerActive=false;

                    //play sound on finish
                    //instead of MainActivity.this, we can also use getApplicationContext()
                    MediaPlayer mPlayer = MediaPlayer.create(MainActivity.this,R.raw.airhorn);
                    mPlayer.start();
                    bview.setText("GO");
                }
            };

            timer.start();
            bview.setText("PAUSE");

        }else{
            //if timer is active, stop timer
            timer.cancel();
            //deactivate timer to enable dragging
            timerActive = false;
            bview.setText("GO");
        }



    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timeBar= (SeekBar) findViewById(R.id.seekBar);
        final TextView timeTxt = (TextView) findViewById(R.id.textView);

        timeBar.setMax(100);

        timeBar.setProgress(30);

        //change time when seekbar changes
        timeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
              timeTxt.setText(Integer.toString(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //disable seekbar dragging when timer is on
        timeBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //true : cannot drag. ie widget is always on touch by event
                //false: can drag
                return timerActive;
            }
        });
    }
}
