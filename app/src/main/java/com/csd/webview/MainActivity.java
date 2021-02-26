package com.csd.webview;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import com.csd.webview.pojo.UrlConfig;
import com.csd.webview.utils.FIieUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * 类名称: MainActivity
 * 类描述: 主页
 * 创建人:
 * 创建时间:
 */
public class MainActivity extends BaseActivity {

    private static final String SEPARATOR =File.separator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_customwebview);

        String fileName = "urlconfig.xml";
        String appFolder = "webapp";
        UrlConfig config =new UrlConfig();
        config.setPageUrl("http://localhost");


        try {
//            String sdcard =FIieUtils.getSDCardPath();
            String storagePath = Environment.getExternalStorageDirectory() + SEPARATOR + appFolder+SEPARATOR;
            File file = new File(storagePath, fileName);
            if (!file.exists()) {
                // 初始化文件目录
                FIieUtils.copyFilesFromAssets(this, fileName, storagePath );

            }

             config = FIieUtils.parserXmlFromLocal(storagePath,fileName);
            if(config==null||config.getPageUrl()==null){
                config.setPageUrl("请检查网络配置");
                config.setPageTitle("请检查网络配置");
            }
            MyWebViewActivity.startActivity(MainActivity.this, config.getPageTitle(), config.getPageUrl());

        } catch (Exception e) {
            e.printStackTrace();
        }

//        setTitle("主页");

        finish();

    }

}
