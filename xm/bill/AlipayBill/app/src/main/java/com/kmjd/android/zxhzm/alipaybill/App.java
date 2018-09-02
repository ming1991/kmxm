package com.kmjd.android.zxhzm.alipaybill;

import android.app.Application;
import android.content.Context;


public class App extends Application {
    public static Context applicationContext;
    @Override
    public void onCreate() {
        super.onCreate();
        if (null == applicationContext){
            applicationContext = getApplicationContext();
        }
    }
}
