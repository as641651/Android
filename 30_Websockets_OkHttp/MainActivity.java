package com.aravindsankaran.websocket;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class MainActivity extends AppCompatActivity {

    private Button mStart;
    private Button mStop;
    private TextView mTextView;
    private OkHttpClient client;
    private WebSocket mWs;


    private final class EchoWebSocketListener extends WebSocketListener{

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            super.onOpen(webSocket, response);
            webSocket.send("Hello!");
            webSocket.send("Connection established!");
            Log.i("Ws", "New ws created");
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            super.onMessage(webSocket, text);
            // on receiving message
            mTextView.setText(mTextView.getText().toString() + "\n" + text);

        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            super.onClosing(webSocket, code, reason);
            mTextView.setText(mTextView.getText().toString() + "\n\n" + reason);
            Log.i("Ws", "ws closed");

        }
    }

    float mTilt;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            mTilt = event.values[0];
            // OkHttp executes send in the background thread
            mWs.send(String.valueOf(mTilt));
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        client = new OkHttpClient();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mTextView = (TextView) findViewById(R.id.output);
        mTextView.setMovementMethod(new ScrollingMovementMethod()); // for scrolling

        mStart = (Button) findViewById(R.id.start_btn);
        mStart.setOnClickListener( new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // create new connection
                Request request = new Request.Builder().url("ws://echo.websocket.org").build();
                EchoWebSocketListener listener = new EchoWebSocketListener();
                mWs = client.newWebSocket(request,listener);
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
                if(mWs != null){
                    mWs.close(1000,"Goodbye");
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
