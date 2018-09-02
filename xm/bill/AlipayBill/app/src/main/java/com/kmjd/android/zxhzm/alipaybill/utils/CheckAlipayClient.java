package com.kmjd.android.zxhzm.alipaybill.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;


import java.util.List;

/**
 * Created by kmjd on 2017/3/10.
 */

public class CheckAlipayClient {

    public static boolean hasAlipayClient(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.eg.android.AlipayGphone")) {
                    return true;
                }
            }
        }

        return false;
    }

}
