package com.engineer.mvp.webview;

import android.util.Log;
import android.webkit.JavascriptInterface;

import androidx.annotation.Keep;

/**
 * Created by Rookie on 2017/8/7.
 */

@Keep
public class LoadHtmlObject {
    private static final String TAG = "LoadHtmlObject";
    private static final int MAX_PREVIEW_LENGTH = 500;

    private volatile String latestHtml = "";

    @Keep
    @JavascriptInterface
    public void printSourceCode(String html) {
        latestHtml = html == null ? "" : html;
        String preview = latestHtml;
        if (preview.length() > MAX_PREVIEW_LENGTH) {
            preview = preview.substring(0, MAX_PREVIEW_LENGTH) + "...";
        }
        Log.d(TAG, "printSourceCode length=" + latestHtml.length() + ", preview=" + preview);
    }

    public String getLatestHtml() {
        return latestHtml;
    }
}
