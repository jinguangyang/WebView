package com.csd.webview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * 类名称: MyWebViewActivity
 * 类描述: WebView加载H5页面
 * 创建人: 陈书东
 * 创建时间: 2018/8/8 08:08
 */
public class MyWebViewActivity extends CustomWebViewActivity {

    public static final String KEY_TITLE = "KEY_TITLE";
    public static final String KEY_URL = "KEY_URL";

    private String title;
    private String url;

    public static void startActivity(Context context, String title, String url) {
        Intent intent = new Intent(context, MyWebViewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TITLE, title);
        bundle.putString(KEY_URL, url);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            title = bundle.getString(KEY_TITLE);
            url = bundle.getString(KEY_URL);
        }
        setTitle(title);
        initDialog(title);
        webView.loadUrl(url);

        //测试加载失败点击重新加载逻辑
//        loadingFailed();
    }

    @Override
    protected void againLoad() {
        super.againLoad();
        webView.loadUrl(url);
    }
}
