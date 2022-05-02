package com.engineer.android.mini.better;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.engineer.android.mini.R;
import com.engineer.android.mini.better.bitmap.BitmapDelegate;

import java.util.HashMap;

public class BetterActivity extends AppCompatActivity {

    private static final String TAG = "BetterActivity";
    private TextView tv;


    private BetterDelegate betterDelegate;

    private final Handler mHandler1 = new Handler(Looper.getMainLooper());
    private final Handler mHandler2 = new Handler(Looper.getMainLooper());
    private final MyRunnable myRunnable1 = new MyRunnable(1, "run1");
    private final MyRunnable myRunnable2 = new MyRunnable(3, "run2");

    private class MyRunnable implements Runnable {
        private int i;
        private String tag;

        MyRunnable(int i, String tag) {
            this.i = i;
            this.tag = tag;
        }

        @Override
        public void run() {
            while (i < 20) {
                try {
                    Thread.sleep(100);
                    Log.e(TAG, "run:" + tag + ", i = " + i);
                    i++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_better);


        tv = findViewById(R.id.tv);
        bitmapStuff();

        showInfo();

        spTest();

        mHandler1.postDelayed(myRunnable1, 2000);
        mHandler2.postDelayed(myRunnable2, 2000);
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy() called");
        super.onDestroy();
//        mHandler1.removeCallbacksAndMessages(myRunnable1);
//        mHandler2.removeCallbacks(myRunnable2);
//        mHandler1.removeCallbacksAndMessages(null);
        mHandler2.removeCallbacksAndMessages(null);
    }

    private void spTest() {
        HashMap<String, Integer> map = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            map.put("index_" + i, i);
        }
        String mapString = JSON.toJSONString(map);
        SharedPreferences sp = getSharedPreferences("local_file", Context.MODE_PRIVATE);
        sp.edit().putString("my_map", mapString).apply();

        String readString = sp.getString("my_map", "");
        Log.e("spTest", "spTest: " + readString);
        HashMap<String, Integer> newmap = JSON.parseObject(readString, HashMap.class);
        Log.e("spTest", "newmap " + newmap.getClass().getName());
        Log.e("spTest", "map " + newmap);
    }

    private void bitmapStuff() {
        ImageView iv = findViewById(R.id.iv);
        betterDelegate = new BitmapDelegate(this);
        BitmapDelegate delegate = (BitmapDelegate) betterDelegate;
        delegate.preloadBitmap();
        Bitmap bitmap = delegate.loadBitmap();
        iv.setImageBitmap(bitmap);
    }


    private void showInfo() {
        if (betterDelegate != null) {
            tv.setText(betterDelegate.info());
        }
        SharedPreferences sp = getSharedPreferences("better", Context.MODE_PRIVATE);
        sp.edit().putBoolean("ok", true).apply();
    }
}