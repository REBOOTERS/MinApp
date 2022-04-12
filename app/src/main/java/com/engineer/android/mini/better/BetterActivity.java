package com.engineer.android.mini.better;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.engineer.android.mini.R;
import com.engineer.android.mini.better.bitmap.BitmapDelegate;

import java.util.HashMap;

public class BetterActivity extends AppCompatActivity {
    private TextView tv;


    private BetterDelegate betterDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_better);


        tv = findViewById(R.id.tv);
        bitmapStuff();

        showInfo();

        spTest();
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