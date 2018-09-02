package com.kmjd.wcqp.single.zxh;

import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.support.multidex.MultiDexApplication;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.proxy.CaptureType;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * Created by zym on 2017/3/15.
 */

public class MainApplication extends MultiDexApplication {
    public static Context applicationContext;
    public static BrowserMobProxy proxy;
    public static int proxyPort = 8888;
    public static Boolean isInitProxy = false;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = getApplicationContext();
        init();
        //解决Android 7.0 API 大于24出现的 FileUriExposedException
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
    }

    private void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                startProxy();
            }
        }).start();
    }



    public void startProxy(){
        try {
            proxy = new BrowserMobProxyServer();
            proxy.setTrustAllServers(true);
            proxy.start(proxyPort);
        } catch (Exception e) {
            // 防止8888已被占用
            Random rand = new Random();
            proxyPort = rand.nextInt(1000) + 8000;
            proxy = new BrowserMobProxyServer();
            proxy.setTrustAllServers(true);
            proxy.start(proxyPort);
        }
        proxy.enableHarCaptureTypes(
                CaptureType.REQUEST_HEADERS, CaptureType.REQUEST_COOKIES, CaptureType.REQUEST_CONTENT,
                CaptureType.RESPONSE_HEADERS, CaptureType.REQUEST_COOKIES, CaptureType.RESPONSE_CONTENT);

        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
                .format(new Date(System.currentTimeMillis()));
        proxy.newHar(time);
        isInitProxy = true;
    }

}
