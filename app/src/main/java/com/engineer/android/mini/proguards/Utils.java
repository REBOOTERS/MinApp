package com.engineer.android.mini.proguards;

import android.content.Context;
import android.util.Log;

/**
 * Created on 2020/8/22.
 *
 * @author rookie
 */
public class Utils {
    private static final String TAG = "Utils";

    public static void test1() {
        Log.d(TAG, "test1() called");
    }

    public static void test3(Context context) {
        int w = context.getResources().getDisplayMetrics().widthPixels;
        int h = context.getResources().getDisplayMetrics().heightPixels;
        Log.d(TAG, "test3: w=" + w + ", h =" + h);
    }

    public static void test2() {
        Log.d(TAG, "test2() called");
    }


    public static class MyBuilder {
        public String name;

        public void setName(String name) {
            this.name = name;
        }
    }
}
