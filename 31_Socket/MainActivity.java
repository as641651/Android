package com.aravindsankaran.socket;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {


    // socket
    private Socket mClient;
    private PrintWriter mPrintWriter;
    private boolean mConnectionException = false;

    // buttons to open and close socket connection
    private Button mStart;
    private Button mStop;

    // sensor data to be sent over socket
    float mTilt=0;
    private SensorManager mSensorManager;
    private Sensor mSensor;

    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            mTilt = event.values[0];

            // socket operations are always done in separate thread
            // otherwise network exception is thrown
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //Log.i("Sensor",String.valueOf(mTilt));
                    if(!mConnectionException) {
                        mPrintWriter.write(String.valueOf(mTilt) + "P");
                        mPrintWriter.flush();
                    }
                }
            }).start();

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mStart = (Button) findViewById(R.id.start_btn);
        mStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // socket connection should always be done in separate thread.
                // otherwise network exception is thrown
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mClient = new Socket("192.168.1.123",23);
                            mPrintWriter = new PrintWriter(mClient.getOutputStream());
                            //mConnectionException = false;
                        } catch (IOException e) {
                            e.printStackTrace();
                            mConnectionException = true;
                        }
                    }
                }).start();

                if(mConnectionException)
                    Toast.makeText(getApplicationContext(),"Connection not available", Toast.LENGTH_LONG).show();

                if(mSensor!=null)
                    mSensorManager.registerListener(mSensorEventListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
        });

        mStop = (Button) findViewById(R.id.stop_btn);
        mStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSensor!=null)
                    mSensorManager.unregisterListener(mSensorEventListener);

                if(mPrintWriter!=null)
                    mPrintWriter.close();
                if(mClient!=null){
                    try {
                        mClient.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        // stop connections when paused
        mStop.callOnClick();
        super.onPause();
    }
}
