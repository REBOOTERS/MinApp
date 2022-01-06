package com.engineer.android.mini.better.bitmap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created on 2021/5/1.
 *
 * @author rookie
 */
public class LargeImageView extends View {
    private static final String TAG = "LargeImageView";

    private BitmapRegionDecoder bitmapRegionDecoder;
    private int imageWidth;
    private int imageHeight;

    private volatile Rect drawRect;

    private BitmapFactory.Options options;

    private MoveGestureDetector moveGestureDetector;

    public LargeImageView(Context context) {
        super(context);
        init(context);
    }

    public LargeImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LargeImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        drawRect = new Rect();
        options = new BitmapFactory.Options();

        moveGestureDetector = new MoveGestureDetector(context, new MoveGestureDetector.SimpleMoveGestureDetector() {
            @Override
            public boolean onMove(MoveGestureDetector detector) {
                int moveX = (int) detector.getMoveX();
                int moveY = (int) detector.getMoveY();

                if (imageWidth > getWidth()) {
                    drawRect.offset(-moveX, 0);
                    checkWidth();
                    invalidate();
                }
                if (imageHeight > getHeight()) {
                    drawRect.offset(0, -moveY);
                    checkHeight();
                    invalidate();
                }

                return true;
            }
        });
    }

    private void checkWidth() {


        Rect rect = drawRect;
        int imageWidth = this.imageWidth;

        if (rect.right > imageWidth) {
            rect.right = imageWidth;
            rect.left = imageWidth - getWidth();
        }

        if (rect.left < 0) {
            rect.left = 0;
            rect.right = getWidth();
        }
    }


    private void checkHeight() {

        Rect rect = drawRect;
        int imageHeight = this.imageHeight;

        if (rect.bottom > imageHeight) {
            rect.bottom = imageHeight;
            rect.top = imageHeight - getHeight();
        }

        if (rect.top < 0) {
            rect.top = 0;
            rect.bottom = getHeight();
        }
    }


    public void setInputStream(InputStream inputStream) {
        try {
            parseBitmapInfo(inputStream);
            bitmapRegionDecoder = BitmapRegionDecoder.newInstance(inputStream, false);

            requestLayout();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        moveGestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        int iw = imageWidth;
        int ih = imageHeight;
        drawRect.left = (iw - w) / 2;
        drawRect.top = (ih - h) / 2;
        drawRect.right = drawRect.left + w;
        drawRect.bottom = drawRect.top + h;

        Log.d(TAG, "w=" + w + ",h=" + h);
        Log.d(TAG, "iw=" + iw + ",ih=" + ih);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bitmapRegionDecoder != null) {
            Log.e(TAG, "onDraw: " + drawRect);
            Bitmap bitmap = bitmapRegionDecoder.decodeRegion(drawRect, options);
            Log.d(TAG, "onDraw() called with: canvas = [" + canvas + "], bitmap =" + bitmap);
            if (bitmap != null) {
                canvas.drawBitmap(bitmap, 0, 0, null);
            }
        }

    }

    private void parseBitmapInfo(InputStream inputStream) {
        BitmapFactory.Options preOptions = new BitmapFactory.Options();
        preOptions.inJustDecodeBounds = true;
        Bitmap b = BitmapFactory.decodeStream(inputStream, null, preOptions);
        imageWidth = preOptions.outWidth;
        imageHeight = preOptions.outHeight;
    }
}
