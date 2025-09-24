package com.engineer.common.widget.cropper;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.view.View.MeasureSpec;

/**
 * Helper class for {@link CropImageView} with static utility methods.
 */
final class CropImageHelper {

    private CropImageHelper() {
    }

    /**
     * Determines the specs for the onMeasure function. Calculates the width or height depending on
     * the mode.
     *
     * @param measureSpecMode The mode of the measured width or height.
     * @param measureSpecSize The size of the measured width or height.
     * @param desiredSize     The desired size of the measured width or height.
     * @return The final size of the width or height.
     */
    static int getOnMeasureSpec(int measureSpecMode, int measureSpecSize, int desiredSize) {
        // Measure Width
        int spec;
        if (measureSpecMode == MeasureSpec.EXACTLY) {
            // Must be this size
            spec = measureSpecSize;
        } else if (measureSpecMode == MeasureSpec.AT_MOST) {
            // Can't be bigger than...; match_parent value
            spec = Math.min(desiredSize, measureSpecSize);
        } else {
            // Be whatever you want; wrap_content
            spec = desiredSize;
        }
        return spec;
    }

    /**
     * Adjust the given image rectangle by image transformation matrix to know the final rectangle of
     * the image.<br>
     * To get the proper rectangle it must be first reset to original image rectangle.
     */
    static void mapImagePointsByImageMatrix(
            Bitmap bitmap,
            float[] imagePoints,
            float[] scaleImagePoints,
            Matrix imageMatrix) {
        imagePoints[0] = 0;
        imagePoints[1] = 0;
        imagePoints[2] = bitmap.getWidth();
        imagePoints[3] = 0;
        imagePoints[4] = bitmap.getWidth();
        imagePoints[5] = bitmap.getHeight();
        imagePoints[6] = 0;
        imagePoints[7] = bitmap.getHeight();
        imageMatrix.mapPoints(imagePoints);
        scaleImagePoints[0] = 0;
        scaleImagePoints[1] = 0;
        scaleImagePoints[2] = 100;
        scaleImagePoints[3] = 0;
        scaleImagePoints[4] = 100;
        scaleImagePoints[5] = 100;
        scaleImagePoints[6] = 0;
        scaleImagePoints[7] = 100;
        imageMatrix.mapPoints(scaleImagePoints);
    }
}
