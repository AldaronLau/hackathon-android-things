package tk.aldaron.whosthere;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.Image;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;


/**
 * This class takes an image and formats it, and then sends it to
 * the facebook API
 */
public class ImagePreprocessor {
    private static final boolean SAVE_PREVIEW_BITMAP = false;

    private Bitmap rgbFrameBitmap;
    private Bitmap croppedBitmap;

    public ImagePreprocessor(int previewWidth, int previewHeight, int croppedWidth,int croppedHeight) {
        this.croppedBitmap = Bitmap.createBitmap(croppedWidth,croppedHeight, Bitmap.Config.ARGB_8888);
        this.rgbFrameBitmap = Bitmap.createBitmap(previewWidth,previewHeight,Bitmap.Config.ARGB_8888);
    }

    public Bitmap preprocessImage(final Image image) {
        if (image == null) {
            return null;
        }

        if(croppedBitmap != null && rgbFrameBitmap != null) {
            ByteBuffer bb = image.getPlanes()[0].getBuffer();
            rgbFrameBitmap = BitmapFactory.decodeStream(new ByteBufferBackedInputStream(bb));
            cropAndRescaleBitmap(rgbFrameBitmap,croppedBitmap,0);
        }

        image.close();

        return croppedBitmap;
    }

    private static class ByteBufferBackedInputStream extends InputStream {
        ByteBuffer buf;

        public ByteBufferBackedInputStream(ByteBuffer buf) {
            this.buf = buf;
        }

        public int read() throws IOException {
            if (!buf.hasRemaining()) {
                return -1;
            }

            return buf.get() & 0xFF;
        }

        public int read(byte[] bytes, int off, int len) throws IOException {
            if (!buf.hasRemaining()) {
                return -1;
            }

            len = Math.min(len, buf.remaining());
            buf.get(bytes, off, len);
            return len;
        }
    }

    /**
     * Saves a bitmap object to the desk
     */
    static void saveBitmap(final Bitmap bitmap) {
        final File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"preview.png");

        file.delete();

        try (FileOutputStream fs = new FileOutputStream(file);
            BufferedOutputStream out = new BufferedOutputStream(fs)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 99, out);
        } catch (Exception e) {
            Log.w("ImageHelper", "Could not save image for debugging", e);
        }
    }


    static void cropAndRescaleBitmap(final Bitmap src, final Bitmap dst,
                                     int sensorOrientation) {
        final float minDim = Math.min(src.getWidth(), src.getHeight());

        final Matrix matrix = new Matrix();

        // We only want the center square out of the original rectangle.
        final float translateX = -Math.max(0, (src.getWidth() - minDim) / 2);
        final float translateY = -Math.max(0, (src.getHeight() - minDim) / 2);
        matrix.preTranslate(translateX, translateY);

        final float scaleFactor = dst.getHeight() / minDim;
        matrix.postScale(scaleFactor, scaleFactor);

        // Rotate around the center if necessary.
        if (sensorOrientation != 0) {
            matrix.postTranslate(-dst.getWidth() / 2.0f, -dst.getHeight() / 2.0f);
            matrix.postRotate(sensorOrientation);
            matrix.postTranslate(dst.getWidth() / 2.0f, dst.getHeight() / 2.0f);
        }

        final Canvas canvas = new Canvas(dst);
        canvas.drawBitmap(src, matrix, null);
    }
}
