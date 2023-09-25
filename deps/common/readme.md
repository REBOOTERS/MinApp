```java
    public void exifInfo() {
        try {
            ExifInterface exifInterface = new ExifInterface(
                    "/sdcard/a.jpg");
            String FFNumber = exifInterface
                    .getAttribute(ExifInterface.TAG_APERTURE);
            String FDateTime = exifInterface
                    .getAttribute(ExifInterface.TAG_DATETIME);
            String FExposureTime = exifInterface
                    .getAttribute(ExifInterface.TAG_EXPOSURE_TIME);
            String FFlash = exifInterface
                    .getAttribute(ExifInterface.TAG_FLASH);
            String FFocalLength = exifInterface
                    .getAttribute(ExifInterface.TAG_FOCAL_LENGTH);
            String FImageLength = exifInterface
                    .getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
            String FImageWidth = exifInterface
                    .getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
            String FISOSpeedRatings = exifInterface
                    .getAttribute(ExifInterface.TAG_ISO);
            String FMake = exifInterface
                    .getAttribute(ExifInterface.TAG_MAKE);
            String FModel = exifInterface
                    .getAttribute(ExifInterface.TAG_MODEL);
            String FOrientation = exifInterface
                    .getAttribute(ExifInterface.TAG_ORIENTATION);
            String FWhiteBalance = exifInterface
                    .getAttribute(ExifInterface.TAG_WHITE_BALANCE);

            Log.i(TAG, "FFNumber:" + FFNumber);
            Log.i(TAG, "FDateTime:" + FDateTime);
            Log.i(TAG, "FExposureTime:" + FExposureTime);
            Log.i(TAG, "FFlash:" + FFlash);
            Log.i(TAG, "FFocalLength:" + FFocalLength);
            Log.i(TAG, "FImageLength:" + FImageLength);
            Log.i(TAG, "FImageWidth:" + FImageWidth);
            Log.i(TAG, "FISOSpeedRatings:" + FISOSpeedRatings);
            Log.i(TAG, "FMake:" + FMake);
            Log.i(TAG, "FModel:" + FModel);
            Log.i(TAG, "FOrientation:" + FOrientation);
            Log.i(TAG, "FWhiteBalance:" + FWhiteBalance);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
```