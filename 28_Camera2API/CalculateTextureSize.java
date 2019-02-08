package com.aravindsankaran.cameraapi;

import android.hardware.camera2.CameraCharacteristics;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CalculateTextureSize {

    private static SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 0);
        ORIENTATIONS.append(Surface.ROTATION_90, 90);
        ORIENTATIONS.append(Surface.ROTATION_180, 180);
        ORIENTATIONS.append(Surface.ROTATION_270, 270);
    }

    public int rotatedWidth;
    public int rotatedHeight;
    public int totalRotation;

    public CalculateTextureSize(CameraCharacteristics cameraCharacteristics,
                                int width,
                                int height,
                                int deviceOrientation){
        // the orientation of device may not match with orientation of camera
        // so we adjust the orientation and use it to calculate preview size
       totalRotation = sensorToDeviceRotation(cameraCharacteristics, deviceOrientation);
        boolean swapRotation = totalRotation == 90 || totalRotation == 270;
        rotatedWidth = width;
        rotatedHeight = height;
        if(swapRotation) {
            rotatedWidth = height;
            rotatedHeight = width;
        }
    }

    private int sensorToDeviceRotation(CameraCharacteristics cameraCharacteristics, int deviceOrientation) {
        int sensorOrienatation = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        deviceOrientation = ORIENTATIONS.get(deviceOrientation);
        return (sensorOrienatation + deviceOrientation + 360) % 360;
    }

    private class CompareSizeByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            return Long.signum( (long)(lhs.getWidth() * lhs.getHeight()) -
                    (long)(rhs.getWidth() * rhs.getHeight()));
        }
    }

    public Size chooseOptimalSize(Size[] choices) {
        List<Size> bigEnough = new ArrayList<Size>();
        for(Size option : choices) {
            float aspectRatio = (float)option.getHeight()/option.getWidth();
            if((aspectRatio <= (rotatedHeight / rotatedWidth) + 0.1*aspectRatio ||
                    aspectRatio >= (rotatedHeight / rotatedWidth) - 0.1*aspectRatio) &&
                    option.getWidth() >= rotatedWidth && option.getHeight() >= rotatedHeight) {
                bigEnough.add(option);
            }
        }
        if(bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizeByArea());
        } else {
            return choices[0];
        }
    }

}
