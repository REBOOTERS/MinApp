package com.engineer.mvp.webview;

import android.app.Activity;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import androidx.annotation.Keep;
import androidx.appcompat.app.AlertDialog;

import java.lang.ref.WeakReference;



/**
 * Created by rookie on 2017/2/13.
 */

@Keep
public class JsObject {
    private static final String TAG = "JsObject";
    private final WeakReference<Context> contextRef;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public JsObject(Context context) {
        contextRef = new WeakReference<>(context);
    }

    @Keep
    @JavascriptInterface
    public void showToast(String content) {
        Context context = contextRef.get();
        if (context == null) {
            Log.w(TAG, "showToast skipped because context is null");
            return;
        }
        String message = TextUtils.isEmpty(content) ? "Hello from Android" : content;
        mainHandler.post(() -> Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_SHORT).show());
    }

    @Keep
    @JavascriptInterface
    public void showDialog() {
        Activity activity = getActivity();
        if (activity == null) {
            Log.w(TAG, "showDialog skipped because activity is unavailable");
            return;
        }
        mainHandler.post(() -> {
            if (activity.isFinishing()) {
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed()) {
                return;
            }
            new AlertDialog.Builder(activity)
                    .setTitle("Exit App")
                    .setMessage("Exit the application ?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false)
                    .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss())
                    .setPositiveButton("sure", (dialog, which) -> {
                        dialog.dismiss();
                        activity.finish();
                    })
                    .show();
        });
    }

    private Activity getActivity() {
        Context context = contextRef.get();
        if (context instanceof Activity activity) {
            return activity;
        }
        return null;
    }
}
