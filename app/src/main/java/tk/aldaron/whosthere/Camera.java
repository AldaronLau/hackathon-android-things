package tk.aldaron.whosthere;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.media.ImageReader;
import android.os.Handler;
import android.util.Log;

public class Camera {

    private static final String TAG = Camera.class.getSimpleName();

    private static final int IMAGE_WIDTH = 320;
    private static final int IMAGE_HEIGHT = 240;
    private static final int MAX_IMAGES = 1;

    private CameraDevice mCameraDevice;

    private CameraCaptureSession mCaptureSession;

    private ImageReader mImageReader;

    private Camera() {

    }

    /**
     * This method creates the camera manager that handles retrieving camera ids
     * @param context activity context
     * @param handler
     * @param imageAvailableListener
     */
    public void initializeCamera(Context context, Handler handler, ImageReader.OnImageAvailableListener imageAvailableListener) {
        CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        String[] cameraIds = {};

        try {
            cameraIds = manager.getCameraIdList();
        } catch (CameraAccessException e) {
            Log.e(TAG, "Cam access exception getting IDs", e);
        }

        if (cameraIds.length < 1) {
            Log.e(TAG, "No cameras found");
        }

        String id = cameraIds[0];
        Log.d(TAG, "Using Camera ID: " + id);

        // initialize the image processor
        mImageReader = ImageReader.newInstance(IMAGE_WIDTH, IMAGE_HEIGHT,ImageFormat.JPEG, MAX_IMAGES);

        // open  the camera source
        try {
            manager.openCamera(id, mStateCallback, handler);
        } catch (CameraAccessException e) {
            Log.d(TAG, "Camera Access Exception", e);
        }
    }
}
