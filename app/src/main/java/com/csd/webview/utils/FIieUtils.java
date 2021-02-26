package com.csd.webview.utils;

import android.os.Environment;
import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

import com.csd.webview.pojo.UrlConfig;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FIieUtils {
    /**
     * 判断SDCard是否存在 [当没有外挂SD卡时，内置ROM也被识别为存在sd卡]
     *
     * @return
     */
    public static boolean isSdCardExist() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取SD卡根目录路径
     *
     * @return
     */
    public static String getSdCardPath() {
        boolean exist = isSdCardExist();
        String sdpath = "";
        if (exist) {
            sdpath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath();
        } else {
            sdpath = "不适用";
        }
        return sdpath;

    }

    private static final String SEPARATOR = File.separator;//路径分隔符

    /**
     * 复制assets中的文件到指定目录
     *
     * @param context     上下文
     * @param assetsPath  assets资源路径
     * @param storagePath 目标文件夹的路径
     */
    public static void copyFilesFromAssets(Context context, String assetsPath, String storagePath) {
        String temp = "";

        if (TextUtils.isEmpty(storagePath)) {
            return;
        } else if (storagePath.endsWith(SEPARATOR)) {
            storagePath = storagePath.substring(0, storagePath.length() - 1);
        }

        if (TextUtils.isEmpty(assetsPath) || assetsPath.equals(SEPARATOR)) {
            assetsPath = "";
        } else if (assetsPath.endsWith(SEPARATOR)) {
            assetsPath = assetsPath.substring(0, assetsPath.length() - 1);
        }

        AssetManager assetManager = context.getAssets();
        try {
            File file = new File(storagePath);
            if (!file.exists()) {//如果文件夹不存在，则创建新的文件夹
                file.mkdirs();
            }

            // 获取assets目录下的所有文件及目录名
            String[] fileNames = assetManager.list(assetsPath);
            if (fileNames.length > 0) {//如果是目录 apk
                for (String fileName : fileNames) {
                    if (!TextUtils.isEmpty(assetsPath)) {
                        temp = assetsPath + SEPARATOR + fileName;//补全assets资源路径
                    }

                    String[] childFileNames = assetManager.list(temp);
                    if (!TextUtils.isEmpty(temp) && childFileNames.length > 0) {//判断是文件还是文件夹：如果是文件夹
                        copyFilesFromAssets(context, temp, storagePath + SEPARATOR + fileName);
                    } else {//如果是文件
//                        String assetRoot = assetManager.
                        File path = new File(storagePath, fileName);
                        FileInputStream fis = new FileInputStream(path);
                        InputStream inputStream = assetManager.open(temp);
                        readInputStream(storagePath + SEPARATOR + fileName, inputStream);
                    }
                }
            } else {//如果是文件 doc_test.txt或者apk/app_test.apk
                InputStream inputStream = assetManager.open(assetsPath);
                if (assetsPath.contains(SEPARATOR)) {//apk/app_test.apk
                    assetsPath = assetsPath.substring(assetsPath.lastIndexOf(SEPARATOR), assetsPath.length());
                }
                readInputStream(storagePath + SEPARATOR + assetsPath, inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 读取输入流中的数据写入输出流
     *
     * @param storagePath 目标文件路径
     * @param inputStream 输入流
     */
    public static void readInputStream(String storagePath, InputStream inputStream) {
        File file = new File(storagePath);
        try {
            if (!file.exists()) {
                // 1.建立通道对象
                FileOutputStream fos = new FileOutputStream(file);
                // 2.定义存储空间
                byte[] buffer = new byte[inputStream.available()];
                // 3.开始读文件
                int lenght = 0;
                while ((lenght = inputStream.read(buffer)) != -1) {// 循环从输入流读取buffer字节
                    // 将Buffer中的数据写到outputStream对象中
                    fos.write(buffer, 0, lenght);
                }
                fos.flush();// 刷新缓冲区
                // 4.关闭流
                fos.close();
                inputStream.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    //正文

    /**
     * 写xml文件到本地
     * 实体person类 (id name age)
     */
    public static void writeXmlToLocal(UrlConfig config, String storagePath, String fileName) {


        // 获得序列化对象
        XmlSerializer serializer = Xml.newSerializer();

        try {
//            File path = new File(Environment.getExternalStorageDirectory() + SEPARATOR + "" + SEPARATOR, fileName);
            File path = new File(storagePath, fileName);
            FileOutputStream fos = new FileOutputStream(path);
            // 指定序列化对象输出的位置和编码
            serializer.setOutput(fos, "utf-8");

            serializer.startDocument("utf-8", true); // 写开始 <?xml version='1.0' encoding='utf-8' standalone='yes' ?>

            //null 命名空间
            serializer.startTag(null, "config");  // <persons>


            // 写名字
            serializer.startTag(null, "pagetitle");  // <name>
            serializer.text(config.getPageTitle());
            serializer.endTag(null, "pagetitle");  // </name>

            // 写url
            serializer.startTag(null, "url");  // <age>
            serializer.text(String.valueOf(config.getPageUrl()));
            serializer.endTag(null, "url");  // </age>
            serializer.endTag(null, "config");   // </persons>

            serializer.endDocument();  // 结束
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //解析xml文件
    public static UrlConfig parserXmlFromLocal(String storagePath,String fileName) {
        try {
            File path = new File(storagePath, fileName);
            FileInputStream fis = new FileInputStream(path);

            // 获得pull解析器对象
            XmlPullParser parser = Xml.newPullParser();
            // 指定解析的文件和编码格式
            parser.setInput(fis, "utf-8");

            int eventType = parser.getEventType();   // 获得事件类型


            UrlConfig config = null;
            String id;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName(); // 获得当前节点的名称

                switch (eventType) {
                    case XmlPullParser.START_TAG:   // 当前等于开始节点  <person>
                        if ("config".equals(tagName)) { // <persons>
                            config = new UrlConfig();
                        } else if ("pagetitle".equals(tagName)) { // <name>
                            config.setPageTitle(parser.nextText());
                        } else if ("url".equals(tagName)) { // <age>
                            config.setPageUrl(parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:  // </persons>
                        if ("config".equals(tagName)) {
                            // 需要把上面设置好值的person对象添加到集合中
                           return config;
                        }
                        break;
                    default:
                        break;
                }

                eventType = parser.next();  // 获得下一个事件类型
            }
            return config;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String getSDCardPath(){
        String cmd = "cat /proc/mounts";
        Runtime run = Runtime.getRuntime();
        BufferedInputStream in=null;
        BufferedReader inBr=null;
        try {
            Process p = run.exec(cmd);
            in = new BufferedInputStream(p.getInputStream());
            inBr = new BufferedReader(new InputStreamReader(in));

            String lineStr;
            while ((lineStr = inBr.readLine()) != null) {
                Log.i("CommonUitls", lineStr);
                if (lineStr.toLowerCase().contains("sdcard".toLowerCase())
                        && lineStr.contains(".android_secure")) {
                    String[] strArray = lineStr.split(" ");
                    if (strArray != null && strArray.length >= 5) {
                        String result = strArray[1].replace("/.android_secure",
                                "");
                        Log.i("CommonUitls", result);
                        return result;
                    }
                }
                if(lineStr.toLowerCase().contains("extsd".toLowerCase())){
                    String[] strArray = lineStr.split(" ");
                    Log.i("CommonUitls", strArray[1]);
                    return strArray[1];
                }
                if (p.waitFor() != 0 && p.exitValue() == 1) {
                    Log.e("CommonUitls", "CMD cat /proc/mounts ERROR!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("CommonUitls", e.toString());
        }finally{
            try {
                if(in!=null){
                    in.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.e("CommonUitls", e.toString());
            }
            try {
                if(inBr!=null){
                    inBr.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.e("CommonUitls", e.toString());
            }
        }
        return Environment.getExternalStorageDirectory().getPath();
    }
}
