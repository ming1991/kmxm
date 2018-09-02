package com.kmjd.wcqp.single.zxh.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


/**
 * Created by xushuai on 2016/11/9.
 */

public class NetWorkUtil {
    //1.检查网络连接,检查网络连接需要权限
    public static boolean checkNetwork(Context context){
        //获取网络管理者
        ConnectivityManager cm= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //获取网络信息
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        if(activeNetworkInfo==null){
            //没有网络
            return false;
        }
        //有网络的话获取网络的类型
        int type = activeNetworkInfo.getType();
        if(type== ConnectivityManager.TYPE_WIFI || type== ConnectivityManager.TYPE_MOBILE){
            //如果是wifi或者是移动网络
            return true;
        }
        return false;
    }

}
