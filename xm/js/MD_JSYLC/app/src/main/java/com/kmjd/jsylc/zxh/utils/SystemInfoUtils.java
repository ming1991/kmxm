package com.kmjd.jsylc.zxh.utils;

/**
 * Created by 80621 on 2018/8/17.
 * 获取手机设备信息工具类
 */

public class SystemInfoUtils {

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    public static String getPhoneModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取手机厂商
     *
     * @return 手机厂商
     */
    public static String getPhoneBrand() {
        return android.os.Build.BRAND;
    }

}
