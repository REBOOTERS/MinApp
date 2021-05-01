package com.engineer.android.mini.better;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.engineer.android.mini.R;
import com.engineer.android.mini.better.bitmap.BitmapDelegate;

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
    }
}