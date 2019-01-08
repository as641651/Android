package com.aravindsankaran.audio;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    MediaPlayer mplayer;
    AudioManager audioManager;


    public void onPlay(View view){
        mplayer.start();
    }

    public void onPause(View view){
        //When we pause when the media is not playing, we get start-exception.
        // Ie, next time we call start, it may not work
        if(mplayer.isPlaying())
            mplayer.pause();
    }

    public void onRestart(View view){
        if(mplayer.isPlaying())
            mplayer.pause();
        mplayer.seekTo(0);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mplayer = MediaPlayer.create(this,R.raw.sample);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        //Get the max volume set in your device for media (music)
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);


        //synchronize with volume with seek bar
        SeekBar volumeControl = (SeekBar) findViewById(R.id.seekBar6);

        //set the max volume as the max of seekbar, which is otherwise between 0 and 100
        volumeControl.setMax(maxVolume);
        //also set the current volume value
        volumeControl.setProgress(currentVolume);

        // new Object() {} : creates an anonymous class extending Object and creates and instance of that class
        volumeControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //progress changes from 0 to 100
                Log.i("volume lvl",Integer.toString(progress));

                //change volume
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //abstract class and needs to be implemented
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //abstract class and needs to be implemented
            }
        });

        //synchronize progress
        final SeekBar progressControl = (SeekBar) findViewById(R.id.seekBar2);

        //set max duration on progress bar
        int duration = mplayer.getDuration();
        progressControl.setMax(duration);

        //sync progress bar with music play
        //this code carries out a specific task every 1s
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                progressControl.setProgress(mplayer.getCurrentPosition());
            }
        },0,1000);

        progressControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mplayer.seekTo(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
