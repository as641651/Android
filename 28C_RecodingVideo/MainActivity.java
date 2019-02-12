package com.aravindsankaran.camera2apipart2;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private TextureView mTextureView;
    private TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            setUpCamera(width,height); // get camera id, preview size
            connectCamera(); // open camera
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    private CameraDevice mCameraDevice;
    private CameraDevice.StateCallback mCameraDeviceStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            mCameraDevice = camera;
            startPreview(); // stream image buffer
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            camera.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            camera.close();
            mCameraDevice = null;
        }
    };

    private String mCameraId;
    private Size mPreviewSize;

    private int mTotalRotation;
    private Size mVideoSize;

    private void setUpCamera(int width, int height){
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            for(String cameraId : cameraManager.getCameraIdList()){
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                if(cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT)
                    continue;

                mCameraId = cameraId;

                int deviceOrientation = getWindowManager().getDefaultDisplay().getRotation();
                CalculateTextureSize calcSize = new CalculateTextureSize(cameraCharacteristics,width,height,deviceOrientation);
                StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                mPreviewSize = calcSize.chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class));

                mTotalRotation = calcSize.totalRotation;
                mVideoSize = calcSize.chooseOptimalSize(map.getOutputSizes(MediaRecorder.class));
                Log.i("video sizes", String.valueOf(mVideoSize.getWidth()) + ", " + String.valueOf(mVideoSize.getHeight()));

                return;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private HandlerThread mBackgroundHandlerThread;
    private Handler mBAckgroundHandler;

    private void startBackgroundThread(){
        mBackgroundHandlerThread = new HandlerThread("cameraapi");
        mBackgroundHandlerThread.start();
        mBAckgroundHandler = new Handler(mBackgroundHandlerThread.getLooper());
    }

    private void stopBackgroundThread(){
        mBackgroundHandlerThread.quitSafely();
        try {
            mBackgroundHandlerThread.join();
            mBackgroundHandlerThread = null;
            mBAckgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void connectCamera(){
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            try {
                cameraManager.openCamera(mCameraId,mCameraDeviceStateCallback,null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }


    private CaptureRequest.Builder mCaptureRequestBuilder;

    private void startPreview(){
        SurfaceTexture surfaceTexture = mTextureView.getSurfaceTexture();
        surfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        Surface previewSurface =  new Surface(surfaceTexture);

        try {
            mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mCaptureRequestBuilder.addTarget(previewSurface);

            mCameraDevice.createCaptureSession(Arrays.asList(previewSurface),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured( CameraCaptureSession session) {
                            try {
                                session.setRepeatingRequest(mCaptureRequestBuilder.build(),null,mBAckgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed( CameraCaptureSession session) {
                            Log.i("Camera config", "Failed");
                        }
                    }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }


    }

    // PART 2 From here

    // set up storage location
    private File mVideoFolder;
    private File mVideoFile;
    private String mVideofileName;

    // to be called under onCreate
    private void createVideoFolder(){
        // get the storage dir for movies
        File moviewFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        // create a new directory for your app under this directory
        mVideoFolder = new File(moviewFile, "camera2videoImage");
        if(!mVideoFolder.exists())
            mVideoFolder.mkdirs();

    }

    // to be called when recording is started
    private void createVideoFileName(){
        //create unique name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String prepend = "VIDEO_" + timeStamp + "_";

        File videoFile = null;
        try {
            videoFile = File.createTempFile(prepend, ".mp4", mVideoFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mVideofileName = videoFile.getAbsolutePath();
        mVideoFile = videoFile;
    }

    private MediaRecorder mMediaRecorder;

    // to be called when recording have to be saved
    private void setUpMediaRecorder(){
        // the order is important
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setOutputFile(mVideofileName);
        mMediaRecorder.setVideoEncodingBitRate(1000000);
        mMediaRecorder.setVideoFrameRate(24);
        mMediaRecorder.setVideoSize(mVideoSize.getWidth(), mVideoSize.getHeight());
        //mMediaRecorder.setVideoSize(640, 480);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mMediaRecorder.setOrientationHint(mTotalRotation);
        try {
            mMediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void startRecord(){

        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this,"No permission to access storage", Toast.LENGTH_SHORT).show();
            return;
        }

        createVideoFileName();

        setUpMediaRecorder();
        // record surface
        Surface recordSurface = mMediaRecorder.getSurface();
        //surface to preview recording
        SurfaceTexture surfaceTexture = mTextureView.getSurfaceTexture();
        surfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        Surface previewSurface =  new Surface(surfaceTexture);

        try {
            mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            mCaptureRequestBuilder.addTarget(previewSurface);
            mCaptureRequestBuilder.addTarget(recordSurface);

            mCameraDevice.createCaptureSession(Arrays.asList(previewSurface, recordSurface),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(CameraCaptureSession session) {
                            try {
                                session.setRepeatingRequest(mCaptureRequestBuilder.build(),null,mBAckgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }

                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mMediaRecorder.start();
                                }
                            });
                        }

                        @Override
                        public void onConfigureFailed(CameraCaptureSession session) {

                        }
                    }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }


    }

    private boolean saveVideo(){
        final boolean[] isSaved = {true};
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_menu_save)
                .setTitle("Save video")
                .setMessage("Save video?")
                .setPositiveButton("Yes", null)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(mVideoFile.exists())
                            mVideoFile.delete();
                        isSaved[0] = false;
                    }
                })
                .show();
        return isSaved[0];
    }

    private void stopRecord(){
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            // open an alert dialog. delete file if save is not intended
            saveVideo();
            //revert back to preview mode
            startPreview();
        }
    }


    ImageButton mRecordButton;
    boolean mIsRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createVideoFolder();

        mMediaRecorder = new MediaRecorder();

        mTextureView = (TextureView) findViewById(R.id.textureView);

        ArrayList<String> permissions = new ArrayList<String>();

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.CAMERA);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.RECORD_AUDIO);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(permissions.size()>0){
            String[] reqPerm = new String[permissions.size()];
            for(int i=0; i<permissions.size(); i++)
                reqPerm[i] = permissions.get(i);
            ActivityCompat.requestPermissions(this, reqPerm,1);
        }


        mRecordButton = (ImageButton) findViewById(R.id.imageButton2);
        mRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIsRecording){
                    mIsRecording = false;
                    mRecordButton.setImageResource(android.R.drawable.presence_video_online);
                    stopRecord();
                }else{
                    mIsRecording = true;
                    mRecordButton.setImageResource(android.R.drawable.presence_video_busy);
                    startRecord();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        onPause();
        onResume();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startBackgroundThread();
        if(!mTextureView.isAvailable()){
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }else{
            setUpCamera(mTextureView.getWidth(), mTextureView.getHeight());
            connectCamera();
        }
    }

    @Override
    protected void onPause() {
        if(mCameraDevice != null){
            mCameraDevice.close();
            mCameraDevice = null;
        }
        if(mBackgroundHandlerThread!=null)
            stopBackgroundThread();
        super.onPause();
    }
}
