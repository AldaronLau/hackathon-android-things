package tk.aldaron.whosthere;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Skeleton of an Android Things activity.
 *
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 *
 * <pre>{@code
 * PeripheralManagerService service = new PeripheralManagerService();
 * mLedGpio = service.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);
 * }</pre>
 *
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 *
 */
public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    /** Camera image capture size */
    private static final int PREVIEW_IMAGE_WIDTH = 640;
    private static final int PREVIEW_IMAGE_HEIGHT = 480;

    private CameraHandler mCameraHandler;
    private Button mCameraButton;
    /**
     * Initialize the camera
     */
    private void initCamera() {
        mCameraHandler = CameraHandler.getInstance();
    }

    /**
     * Clean up the resources used by the camera
     */
    private void closeCamera() {
        mCameraHandler.shutDown();
    }

    /**
     * load the image that will be used in the classification process
     */
    private void loadPhoto() {
        mCameraHandler.takePicture();
        Bitmap bitmap = mCameraHandler.
        onPhotoReady();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCameraButton = findViewById(R.id.button);

        mCameraButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 mCameraHandler.takePicture();
             }
         }

        );
        initCamera();
    }

}
