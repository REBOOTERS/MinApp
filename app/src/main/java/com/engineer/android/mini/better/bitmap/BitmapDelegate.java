package com.engineer.android.mini.better.bitmap;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.engineer.android.mini.R;
import com.engineer.android.mini.better.BetterDelegate;

/**
 * Created on 2021/5/1.
 *
 * @author rookie
 */
public class BitmapDelegate extends BetterDelegate {
    private static final String TAG = "BitmapDelegate";
    private final Context context;
    private Bitmap bitmap;

    public BitmapDelegate(Context c) {
        this.context = c;
    }

    public void preloadBitmap() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.android, options);
        Log.d(TAG, "preloadBitmap() called " + bitmap);
        Log.d(TAG, "preloadBitmap() called " + options.inSampleSize);
        Log.d(TAG, "preloadBitmap() called " + options.inPreferredConfig.name());
        Log.d(TAG, "preloadBitmap() called " + options.inDensity);
        Log.d(TAG, "preloadBitmap() called " + options.inTargetDensity);
        Log.d(TAG, "preloadBitmap() called " + options.inScaled);
        Log.d(TAG, "preloadBitmap() called " + options.outMimeType);
        Log.d(TAG, "preloadBitmap() called " + options.outWidth);
        Log.d(TAG, "preloadBitmap() called " + options.outHeight);

    }

    public Bitmap loadBitmap() {
        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inScaled = false;
//        options.inSampleSize = 2;
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.android, options);
        return bitmap;
    }

    public Bitmap loadBitmap(String path) {
        bitmap = BitmapFactory.decodeFile(path);
        return bitmap;
    }

    private Resources getResources() {
        return context.getResources();
    }

    @Override
    public String info() {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int a = width * height * 4;
        StringBuilder sb = new StringBuilder();
        sb.append("real=").append(a).append("\n");
        sb.append("width=").append(width).append("\n");
        sb.append("height=").append(height).append("\n");
        sb.append("density=").append(bitmap.getDensity()).append("\n");
        sb.append("size=").append(bitmap.getAllocationByteCount() / ONE_MB).append("MB\n");
        sb.append("byte=").append(bitmap.getAllocationByteCount());
        sb.append("\n\n");
        sb.append("sys-density=").append(getResources().getDisplayMetrics().density).append("\n");
        int targetDensityDpi = getResources().getDisplayMetrics().densityDpi;
        sb.append("sys-densityDpi=").append(targetDensityDpi).append("\n");
        float scale = targetDensityDpi / 640f;
        int w = (int) (1200 * scale);
        int h = (int) (800 * scale);
        sb.append("scale=").append(scale).append(" w=").append(w).append(" h=").append(h).append("\n");


        return sb.toString();
    }
}
