package com.kmjd.jsylc.zxh.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import java.util.List;

/**
 * Created by kmjd on 2017/3/10.
 */

/**
 * Created by Android-Star.
 * 判断微信是否安装
 */
public class CheckWhetherInstalledAnApplication {

    public static String PACKAGE_NAME_WX = "com.tencent.mm";//微信
    public static String PACKAGE_NAME_QQ = "com.tencent.mobileqq";//QQ
    public static String PACKAGE_NAME_QQ_LITE = "com.tencent.qqlite";//QQ轻聊版
    public static String PACKAGE_NAME_TIM = "com.tencent.tim";//TIM

    public static boolean isWeiXinClientAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals(PACKAGE_NAME_WX)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断QQ是否安装
     */
    public static boolean isQQClientAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals(PACKAGE_NAME_QQ)||pn.equals(PACKAGE_NAME_QQ_LITE)||pn.equals(PACKAGE_NAME_TIM)) {
                    return true;
                }
            }
        }
        return false;
    }

}
