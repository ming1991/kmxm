package com.kmjd.android.zxhzm.alipaybill.utils;


import android.content.Context;

import com.kmjd.android.zxhzm.alipaybill.App;


/**
 * Created by 管理员 on 2016/11/9.
 */

public class DpPxUtil {

    public static float getDensity() {
        return density;
    }

    public static void setDensity(float density) {
        DpPxUtil.density = density;
    }

    public static float getScaledDensity() {
        return scaledDensity;
    }

    public static void setScaledDensity(float scaledDensity) {
        DpPxUtil.scaledDensity = scaledDensity;
    }

    private static float density;
    private static float scaledDensity;
    static {
        density = App.applicationContext.getResources().getDisplayMetrics().density;
        scaledDensity = App.applicationContext.getResources().getDisplayMetrics().scaledDensity;
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
