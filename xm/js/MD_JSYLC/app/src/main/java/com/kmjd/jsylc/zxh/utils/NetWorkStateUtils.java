package com.kmjd.jsylc.zxh.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;

/**
 * Created by Android-d on 2017/12/21.
 */

public class NetWorkStateUtils {

    /**
     * @return 是否有活动的网络连接
     */
    public static boolean hasNetWorkConnectionIsAvailable(Context context) {
        //获取连接活动管理器
        final ConnectivityManager connectivityManager = (ConnectivityManager) context.
                getSystemService(Context.CONNECTIVITY_SERVICE);
        //获取链接网络信息
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return (networkInfo != null && networkInfo.isConnected() && networkInfo.isAvailable());
    }

    /**
     * @return 返回boolean ,是否为wifi网络
     */
    public static boolean hasWifiConnectionIsAvailable(Context context) {
        final ConnectivityManager connectivityManager = (ConnectivityManager) context.
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        }
        //是否有网络并且已经连接
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }

    /**
     * @return 返回boolean, 判断网络是否可用, 是否为移动网络
     */

    public static boolean hasMobileConnectionIsAvailable(Context context) {
        //获取活动连接管理器
        final ConnectivityManager connectivityManager = (ConnectivityManager) context.
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        }
        return (networkInfo != null && networkInfo.isAvailable());
    }

    /**
     * @return 判断网络是否可用，并返回网络类型，ConnectivityManager.TYPE_WIFI，ConnectivityManager.TYPE_MOBILE，不可用返回-1
     */
    public static int getNetWorkConnectionType(Context context) {
        final ConnectivityManager connectivityManager = (ConnectivityManager) context.
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = null;
        NetworkInfo mobileNetworkInfo = null;
        if (connectivityManager != null) {
            wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            mobileNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        }

        if (wifiNetworkInfo != null && wifiNetworkInfo.isAvailable()) {
            return ConnectivityManager.TYPE_WIFI;
        } else if (mobileNetworkInfo != null && mobileNetworkInfo.isAvailable()) {
            return ConnectivityManager.TYPE_MOBILE;
        } else {
            return -1;
        }
    }

    /**
     * 判断设备 是否使用代理上网
     * @param context
     * @return
     */
    public static boolean isWifiProxy(Context context) {
        final boolean IS_ICS_OR_LATER = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
        String proxyAddress;
        int proxyPort;
        if (IS_ICS_OR_LATER) {
            proxyAddress = System.getProperty("http.proxyHost");
            String portStr = System.getProperty("http.proxyPort");
            proxyPort = Integer.parseInt((portStr != null ? portStr : "-1"));
        } else {
            proxyAddress = android.net.Proxy.getHost(context);
            proxyPort = android.net.Proxy.getPort(context);
        }
        return (!TextUtils.isEmpty(proxyAddress)) && (proxyPort != -1);
    }
}
