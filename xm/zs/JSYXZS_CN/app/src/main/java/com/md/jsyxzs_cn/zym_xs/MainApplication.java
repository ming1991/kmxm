package com.md.jsyxzs_cn.zym_xs;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

/**
 * Created by KMJD on 2017/2/14.
 */

public class MainApplication extends Application {
    @SuppressLint("StaticFieldLeak")
    private static Context context ;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        //解决Android 7.0 API 大于24出现的 FileUriExposedException
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
    }

    public static Context getMainApplicationContext(){
        return context;
    }
}
