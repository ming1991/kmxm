package com.kmjd.jsylc.zxh.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.kmjd.jsylc.zxh.MainApplication;


/**
 * Created by xushuai on 2016/11/9.
 */

public class NetWorkUtil {
    //1.检查网络连接,检查网络连接需要权限
    public static boolean checkNetwork(){
        //获取网络管理者
        ConnectivityManager cm= (ConnectivityManager) MainApplication.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        //获取网络信息
        NetworkInfo activeNetworkInfo = null;
        if (cm != null) {
            activeNetworkInfo = cm.getActiveNetworkInfo();
        }
        if(activeNetworkInfo==null){
            //没有网络
            return false;
        }
        //有网络的话获取网络的类型
        int type = activeNetworkInfo.getType();
        return type == ConnectivityManager.TYPE_WIFI || type == ConnectivityManager.TYPE_MOBILE;
    }
}
