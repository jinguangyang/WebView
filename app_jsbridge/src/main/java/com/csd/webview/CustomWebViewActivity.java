package com.csd.webview;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.csd.webview.widget.CustomDialogwithBtn;

/**
 * 类名称: CustomWebViewActivity
 * 类描述: 具有统一布局的的WebView页面
 * 创建人: 陈书东
 * 创建时间: 2018/8/8 08:08
 */

public class CustomWebViewActivity extends BaseWebViewActivity {

    protected RelativeLayout rl_loading_faild;
    protected CustomDialogwithBtn customDialogwithBtn;
    protected LinearLayout app_left;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customwebview);
        initViews();
    }

    private void initViews() {
        rl_loading_faild = (RelativeLayout) findViewById(R.id.rl_loading_faild);
        app_left = (LinearLayout) findViewById(R.id.app_left);
        app_left.setVisibility(View.VISIBLE);
        app_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCommonDialog();
            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    finish();
                }
            }
        });
        rl_loading_faild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                againLoad();
            }
        });
    }

    protected void initDialog(String title) {
        customDialogwithBtn = new CustomDialogwithBtn(this, null, "确认退出" + title + "?", "取消", "确定", true, true, true, true);
        customDialogwithBtn.setCancelListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customDialogwithBtn != null) {
                    customDialogwithBtn.dismiss();
                }
            }
        });
        customDialogwithBtn.setOkListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customDialogwithBtn != null) {
                    customDialogwithBtn.dismiss();
                }
                finish();
            }
        });
    }

    protected void showCommonDialog() {
        if (!isFinishing() && customDialogwithBtn != null && !customDialogwithBtn.isShowing()) {
            customDialogwithBtn.show();
        }
    }

    protected void loadingFailed() {
        if (webView != null) {
            webView.setVisibility(View.GONE);
        }
        rl_loading_faild.setVisibility(View.VISIBLE);
    }

    protected void againLoad() {
        if (webView != null) {
            webView.setVisibility(View.VISIBLE);
        }
        rl_loading_faild.setVisibility(View.GONE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (customDialogwithBtn != null && customDialogwithBtn.isShowing()) {
            customDialogwithBtn.dismiss();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (islandport) {
                if (chromeClient != null) {
                    chromeClient.onHideCustomView();
                }
            } else {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    showCommonDialog();
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
