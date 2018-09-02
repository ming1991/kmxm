package com.md.jsyxzs_cn.zym_xs.utils;


import android.content.Context;

import com.md.jsyxzs_cn.zym_xs.MainApplication;


/**
 * Created by 管理员 on 2016/11/9.
 */

public class DpPxUtil {

    private static Context context = MainApplication.getMainApplicationContext();
    public static float density;
    public static float scaledDensity;
    static {
        density = context.getResources().getDisplayMetrics().density;
        scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
    }
    public static int getPxByDp(float dipValue){
        return (int) (dipValue*density+0.5f);
    }

    public static int getDpByPx(float pxValue){
        return (int) (pxValue/density+0.5f);
    }

    public static int getPxBySp(float spValue) {
        return (int) (spValue * scaledDensity + 0.5f);
    }

    public static int getSpByPx(float pxValue) {
        return (int) (pxValue / scaledDensity + 0.5f);
    }


}
