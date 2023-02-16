package com.engineer.common.utils


import android.util.Log
import androidx.exifinterface.media.ExifInterface

object PictureInfoUtil {
    private const val TAG = "PictureInfoUtil"

    fun printExifInfo(path: String) {
        try {
            val exifInterface = ExifInterface(path)
            val FFNumber: String? = exifInterface.getAttribute(ExifInterface.TAG_F_NUMBER)
            val FDateTime: String? = exifInterface.getAttribute(ExifInterface.TAG_DATETIME)
            val FExposureTime: String? = exifInterface.getAttribute(ExifInterface.TAG_EXPOSURE_TIME)
            val FFlash: String? = exifInterface.getAttribute(ExifInterface.TAG_FLASH)
            val FFocalLength: String? = exifInterface.getAttribute(ExifInterface.TAG_FOCAL_LENGTH)
            val FImageLength: String? = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH)
            val FImageWidth: String? = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH)
            val FISOSpeedRatings: String? = exifInterface.getAttribute(ExifInterface.TAG_PHOTOGRAPHIC_SENSITIVITY)
            val FMake: String? = exifInterface.getAttribute(ExifInterface.TAG_MAKE)
            val FModel: String? = exifInterface.getAttribute(ExifInterface.TAG_MODEL)
            val FOrientation: String? = exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION)
            val FWhiteBalance: String? = exifInterface.getAttribute(ExifInterface.TAG_WHITE_BALANCE)

            val lat = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE)
            val latRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF)
            val longt = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)
            val longtRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF)
            Log.i(TAG, "FFNumber:$FFNumber")
            Log.i(TAG, "FDateTime:$FDateTime")
            Log.i(TAG, "FExposureTime:$FExposureTime")
            Log.i(TAG, "FFlash:$FFlash")
            Log.i(TAG, "FFocalLength:$FFocalLength")
            Log.i(TAG, "FImageLength:$FImageLength")
            Log.i(TAG, "FImageWidth:$FImageWidth")
            Log.i(TAG, "FISOSpeedRatings:$FISOSpeedRatings")
            Log.i(TAG, "FMake:$FMake")
            Log.i(TAG, "FModel:$FModel")
            Log.i(TAG, "FOrientation:$FOrientation")
            Log.i(TAG, "FWhiteBalance:$FWhiteBalance")
            Log.i(TAG, "lat = " + lat)
            Log.i(TAG, "latRef = " + latRef)
            Log.i(TAG, "longt = " + longt)
            Log.i(TAG, "longtRef = " + longtRef)
        } catch (e: Exception) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
    }

    private fun convertRationalLatLonToFloat(
        rationalString: String, ref: String
    ): Float {
        val parts = rationalString.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var pair: Array<String>
        pair = parts[0].split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val degrees = pair[0].trim { it <= ' ' }.toDouble() / pair[1].trim { it <= ' ' }.toDouble()
        pair = parts[1].split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val minutes = pair[0].trim { it <= ' ' }.toDouble() / pair[1].trim { it <= ' ' }.toDouble()
        pair = parts[2].split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val seconds = pair[0].trim { it <= ' ' }.toDouble() / pair[1].trim { it <= ' ' }.toDouble()
        val result = degrees + minutes / 60.0 + seconds / 3600.0
        return if (ref == "S" || ref == "W") {
            -result.toFloat()
        } else result.toFloat()
    }
}