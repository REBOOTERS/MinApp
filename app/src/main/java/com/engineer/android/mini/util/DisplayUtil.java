package com.engineer.android.mini.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class DisplayUtil {
    private static final String TAG = "DisplayUtil";

    public DisplayUtil() {
    }

    public static int sVisibleHeight = 0;

    /**
     * @deprecated
     */
    @Deprecated
    public static int dip2px(Context context, float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5F);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public static float px2dip(Context context, float pxVal) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (float) ((int) (pxVal / scale + 0.5F));
    }

    public static int dp2px(float dp) {
        float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5F);
    }

    public static float px2dp(float pxVal) {
        float scale = Resources.getSystem().getDisplayMetrics().density;
        return (float) ((int) (pxVal / scale + 0.5F));
    }

    public static int sp2px(float sp) {
        float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (sp * scale + 0.5F);
    }

    public static float px2sp(float pxVal) {
        return pxVal / Resources.getSystem().getDisplayMetrics().density;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public static int sp2px(Context context, float sp) {
        float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * scale + 0.5F);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public static float px2sp(Context context, float pxVal) {
        return pxVal / context.getApplicationContext().getResources().getDisplayMetrics().scaledDensity;
    }

    public static Point getScreenRealSize(Activity activity) {
        Point outSize = new Point();
        if (Build.VERSION.SDK_INT >= 17) {
            activity.getWindowManager().getDefaultDisplay().getRealSize(outSize);
        } else {
            outSize.x = getScreenWidth();
            outSize.y = getScreenHeight();
        }

        return outSize;
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    protected void transparentStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = activity.getWindow();
            window.clearFlags(67108864);
            window.getDecorView().setSystemUiVisibility(1280);
            window.addFlags(-2147483648);
            window.setStatusBarColor(0);
        }

    }

    /**
     * @deprecated
     */
    @Deprecated
    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        if (statusBarHeight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                statusBarHeight = context.getApplicationContext().getResources().getDimensionPixelSize(x);
            } catch (Exception var6) {
                var6.printStackTrace();
            }
        }

        return statusBarHeight;
    }

    public static int getStatusBarHeight2(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }

        return result;
    }

    public static int getNavigationBarHeight(Context context) {
        int rid = context.getResources().getIdentifier("config_showNavigationBar", "bool", "android");
        if (rid != 0) {
            int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            return context.getResources().getDimensionPixelSize(resourceId);
        } else {
            return 0;
        }
    }


    public static boolean isNavigationBarShowing(Context context) {
        boolean haveNavigationBar = checkDeviceHasNavigationBar(context);
        if (haveNavigationBar && Build.VERSION.SDK_INT >= 17) {
            String brand = Build.BRAND;
            String deviceInfo;
            if (brand != null) {
                if ("HUAWEI".equalsIgnoreCase(brand)) {
                    deviceInfo = "navigationbar_is_min";
                } else if ("XIAOMI".equalsIgnoreCase(brand)) {
                    deviceInfo = "force_fsg_nav_bar";
                } else if ("VIVO".equalsIgnoreCase(brand)) {
                    deviceInfo = "navigation_gesture_on";
                } else if ("OPPO".equalsIgnoreCase(brand)) {
                    deviceInfo = "navigation_gesture_on";
                } else {
                    deviceInfo = "navigationbar_is_min";
                }

                return Settings.Global.getInt(context.getContentResolver(), deviceInfo, 0) == 0;
            }

        }

        return false;
    }

    public static boolean isNavigationBarShow(Activity activity) {
        int realWindowHeight = getScreenRealSize(activity).y;
        int decorViewHeight = activity.getWindow().getDecorView().getHeight();
        Log.e(TAG, "realWindowHeight = " + realWindowHeight + ",decorViewHeight = " + decorViewHeight);
        return realWindowHeight != decorViewHeight;

    }

    private static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }

        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception var7) {
        }

        return hasNavigationBar;
    }
}
