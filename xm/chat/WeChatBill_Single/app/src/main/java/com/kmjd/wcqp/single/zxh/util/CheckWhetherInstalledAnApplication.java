package com.kmjd.wcqp.single.zxh.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.kmjd.wcqp.single.zxh.model.CLASS_ID_TEXT;

import java.util.List;

/**
 * Created by kmjd on 2017/3/10.
 */

public class CheckWhetherInstalledAnApplication {

    public static boolean isWeiXinClientAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals(CLASS_ID_TEXT.PACKAGE_NAME_WX)) {
                    return true;
                }
            }
        }

        return false;
    }

}
