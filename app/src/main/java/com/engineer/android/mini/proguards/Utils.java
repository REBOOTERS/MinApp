package com.engineer.android.mini.proguards;

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
