package com.engineer.mvp.webview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.engineer.mvp.webview.util.FileUtil;
import com.engineer.mvp.webview.util.Tools;


public class AllWebViewActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AllWebViewActivity";

    private static final String WEB_URL = "https://www.baidu.com";
    private static final String PDF_URL = "http://ei-test.51fapiao.cn:9080/FPFX/actions/dd05d5e72d35f0dac23f6362f05f85cb834110";
    private static final String ERROR_URL = "https://www.badu.com";
    private static final String TWXQ = "file:///android_asset/twxq_1.html";
    private static final String JIANSHU = "file:///android_asset/a.html";
    private static final String LOCAL_URL = "file:///android_asset/index.html";
    private static final String ALI_PAY_URL = "file:///android_asset/launch_alipay_app.html";
    private static final String THREE_D_URL = "file:///android_asset/keyframe.html";
    private static final String WEIXIN_PAY_URL = "http://wechat.66card.com/vcweixin/common/toTestH5Weixin?company=c4p ";

    private Context mContext;
    private WebView mWebView;
    private Button mButton;

    private LinearLayout tools;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_all_web_view);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        setTitle("WebView");
        mWebView = findViewById(R.id.webview);
        mButton = findViewById(R.id.button);
        mButton.setOnClickListener(this);
        findViewById(R.id.save).setOnClickListener(this);


        setUpWebView();
        loadData();
    }

    private void setUpWebView() {
        tools = (LinearLayout) findViewById(R.id.tools);
        tools.setVisibility(View.VISIBLE);

        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(false);
        //
        mWebView.addJavascriptInterface(new JsObject(mContext), "myObj");
        mWebView.addJavascriptInterface(new LoadHtmlObject(), "myHtml");
        //

        mWebView.setWebViewClient(new MyWebViewClient(mContext));
        mWebView.setWebChromeClient(new MyWebChromeClient());
        WebView.setWebContentsDebuggingEnabled(true);

    }

    private void loadData() {
        mWebView.loadUrl(LOCAL_URL);
    }


    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.men_web_load_url, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //<editor-fold desc="OptionMenu Item Click Event">
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
            } else {
                finish();
            }
        } else if (id == R.id.local) {
            mWebView.loadUrl(LOCAL_URL);
            tools.setVisibility(View.VISIBLE);
        } else if (id == R.id.jianshu) {
            mWebView.loadUrl(Constant.URL);
            tools.setVisibility(View.GONE);
        } else if (id == R.id.jianshu_local) {
            String text = Tools.readStrFromAssets("a.html", this);
            mWebView.loadDataWithBaseURL("", text, "text/html", "UTF-8", "");
            tools.setVisibility(View.GONE);
        } else if (id == R.id.twxq) {
            mWebView.loadUrl(TWXQ);
            tools.setVisibility(View.GONE);
        } else if (id == R.id.pdf) {
            mWebView.loadUrl(PDF_URL);
            tools.setVisibility(View.VISIBLE);
        } else if (id == R.id.net) {
            mWebView.loadUrl(WEB_URL);
            tools.setVisibility(View.VISIBLE);
        } else if (id == R.id.error) {
            mWebView.loadUrl(ERROR_URL);
            tools.setVisibility(View.GONE);
        } else if (id == R.id.weixinpay) {
            mWebView.loadUrl(WEIXIN_PAY_URL);
            tools.setVisibility(View.GONE);
        } else if (id == R.id.alipay) {
            mWebView.loadUrl(ALI_PAY_URL);
            tools.setVisibility(View.GONE);
        } else if (id == R.id.menu) {
            startActivity(new Intent(mContext, WebViewMenuActivity.class));
        } else if (id == R.id.galaxy) {
            mWebView.loadUrl(THREE_D_URL);
            tools.setVisibility(View.GONE);
            setTitle("galaxy");
        }
        return super.onOptionsItemSelected(item);
    }
    //</editor-fold>

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button) {
            String time = Tools.getCurrentTime();
            String version = Tools.getAppVersion(mContext);
            String name = Tools.getName(mContext);
            String currentUrl = mWebView.getUrl();
            String info = "Application Info: \n\n version: " + version + "\n name: " + name + "\n curTime: " + time + "\n curUrl: " + currentUrl;
            mWebView.loadUrl("javascript:showAlert('" + info + "')");
        } else if (id == R.id.save) {
            Picture snapShot = mWebView.capturePicture();
            Bitmap bmp = Bitmap.createBitmap(snapShot.getWidth(), snapShot.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bmp);
            snapShot.draw(canvas);
            if (!TextUtils.isEmpty(FileUtil.savaBitmap2SDcard(mContext, bmp, "1111"))) {
                Toast.makeText(mContext, "success", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
