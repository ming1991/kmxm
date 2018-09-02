package com.kmjd.jsylc.zxh.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import com.kmjd.jsylc.zxh.MainApplication;

import java.lang.reflect.Method;

/**
 * Created by androidshuai on 2017/12/1.
 */

public class MobileHeightUtil {

    //获取屏幕原始尺寸高度，包括虚拟功能键高度
    public static int getDpi(Context context){
        int dpi = 0;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = null;
        if (windowManager != null) {
            display = windowManager.getDefaultDisplay();
        }else {
            return 0;
        }
        DisplayMetrics displayMetrics = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics",DisplayMetrics.class);
            method.invoke(display, displayMetrics);
            dpi=displayMetrics.heightPixels;
        }catch(Exception e){
            e.printStackTrace();
        }
        return dpi;
    }

    /**
     * 获取 虚拟按键的高度
     * @param context
     * @return
     */
    public static  int getBottomStatusHeight(Context context){
        int totalHeight = getDpi(context);

        int contentHeight = getScreenHeight(context);

        return totalHeight  - contentHeight;
    }


    /**
     * 获得状态栏的高度
     *
     * @param context
     * @return
     */
    public static int getStatusHeight(Context context) {

        int statusHeight = -1;
        //获取status_bar_height资源的ID
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        return statusHeight;
    }

    /**
     * 获得屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        if (wm != null) {
            wm.getDefaultDisplay().getMetrics(outMetrics);
        }
        return outMetrics.heightPixels;
    }

    /**
     * 获得屏幕宽度
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context){
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        if (wm != null) {
            wm.getDefaultDisplay().getMetrics(outMetrics);
        }
        return outMetrics.widthPixels;
    }

    /**
     * 获取键盘的高度<实时获取>
     * @param context
     * @return
     */
    public static int getKeyboardHeight(Activity context){
        Rect rect = new Rect();
        context.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        //计算出可见屏幕的高度
        int displayHight = rect.bottom - rect.top;
        //获取底部虚拟导航键的高度
        int bottomStatusHeight = MobileHeightUtil.getBottomStatusHeight(context);
        //获取状态栏高度
        int statusHeight = MobileHeightUtil.getStatusHeight(context);
        //获得屏幕整体的高度(包含状态栏高度和虚拟按键的高度)
        int hight = context.getWindow().getDecorView().getHeight();
        //获得键盘高度
        int keyboardHeight = hight - displayHight - bottomStatusHeight - statusHeight;
        return keyboardHeight;
    }

    /**
     * 获取LoginActivity界面的多余高度 = 屏幕整体的高度 - 虚拟导航键的高度 - 状态栏的高度 - 430dp
     *      (440=4+52+150+30+45+15+15+45+18+15+45+6)
     * @param context
     * @return
     */
    public static int getSurplusHeight(Activity context){
        int pxBy430Dp = DpPxUtil.getPxByDp(388 + SPUtils.getInt(MainApplication.applicationContext, SPUtils.SP_KEY.TOOLBAR_HEIGHT.toString()));
        //获取底部虚拟导航键的高度
        int bottomStatusHeight = MobileHeightUtil.getBottomStatusHeight(context);
        //获取状态栏高度
        int statusHeight = MobileHeightUtil.getStatusHeight(context);
        //获得屏幕整体的高度(包含状态栏高度和虚拟按键的高度)
        int hight = context.getWindow().getDecorView().getHeight();
        return hight - bottomStatusHeight - statusHeight - pxBy430Dp;
    }

    //获取屏幕原始尺寸高度，包括虚拟功能键高度
    public static int getRealDisplayHeight(Activity activity){
        int realDisplayHeight = 0;
        DisplayMetrics metrics = new DisplayMetrics();
        //获取当前屏幕的真实高度（包含虚拟导航栏）
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        realDisplayHeight = metrics.heightPixels;
        return realDisplayHeight;
    }

    //获取屏幕真是高度
    public static int getDisplayHeight(Activity activity) {
        int displayHeight = 0;
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        //不包括虚拟导航栏的显示屏高度
        displayHeight = metrics.heightPixels;//app的显示高度
        return displayHeight;
    }

    /**
     * 获取状态栏高度——方法1
     */
    public static int getStatusBarHeight_1(Activity activity) {

        int statusBarHeight = 0;
        //获取status_bar_height资源的ID
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = activity.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    //获取状态栏高度——方法2
    public static int getStatusBarHeight_2(Activity activity) {
        int statusBarHeight2 = 0;

        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int resourceId = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
            statusBarHeight2 = activity.getResources().getDimensionPixelSize(resourceId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusBarHeight2;
    }

    /**
     * 获取状态栏高度——方法3
     * 应用区的顶端位置即状态栏的高度
     * *注意*该方法不能在初始化的时候用
     */
    public static int getStatusBarHeight_3(Activity activity) {
        int statusBarHeight3 = 0;
        Rect rectangle = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rectangle);
        //高度为rectangle.top-0仍为rectangle.top
        statusBarHeight3 = rectangle.top;
        return statusBarHeight3;
    }

    /**
     * 获取状态栏高度——方法4
     * 状态栏高度 = 屏幕高度 - 应用区高度
     * *注意*该方法不能在初始化的时候用
     **/
    public static int getStatusBarHeight_4(Activity activity) {
        int statusBarHeight4 = 0;

        //屏幕
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int displayHeight = dm.heightPixels;
        //应用区域
        Rect outRect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
        int appHeight = outRect.height();

        statusBarHeight4 = displayHeight - appHeight;//状态栏高度=屏幕高度-应用区域高度
        return statusBarHeight4;
    }

    public static int getTitleBarHeight_1(Activity activity) {
        int titleBarHeight = 0;
        //获取应用的高度
        Rect rectangle = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rectangle);
        int appHeight = rectangle.height();
        //获取可绘制view（app的高度除去标题栏的高度）的高度
        int viewContentHeight = activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getHeight();
        titleBarHeight = appHeight - viewContentHeight;
        return titleBarHeight;
    }

    public static int getTitleBarHeight_2(Activity activity) {
        int titleBarHeight = 0;
        titleBarHeight = activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
        return titleBarHeight;
    }

    public static int getAppHeight(Activity activity){
        int appHeight = 0;
        //获取应用的高度
        Rect rectangle = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rectangle);
        appHeight = rectangle.height();
        return appHeight;
    }

    public static int getViewContentHeight(Activity activity){
        int viewContentHeight = 0;
        //获取可绘制view（app的高度除去标题栏的高度）的高度
        viewContentHeight = activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getHeight();
        return viewContentHeight;
    }

    public static int getNavigationBarHeight(Activity activity) {
        int navigationBarHeight = 0;
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        //不包括虚拟导航栏的显示屏高度
        int dispalyHeight = metrics.heightPixels;//app的显示高度
        //获取当前屏幕的真实高度（包含虚拟导航栏）
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realDisplayHeight = metrics.heightPixels;
        if (realDisplayHeight > dispalyHeight) {
            navigationBarHeight = realDisplayHeight - dispalyHeight;
            return navigationBarHeight;
        } else {
            return navigationBarHeight;
        }
    }


    /**
     * 获取导航栏高度
     * @param context
     * @return
     */
    public static int getDaoHangHeight(Context context) {
        int resourceId=0;
        int rid = context.getResources().getIdentifier("config_showNavigationBar", "bool", "android");
        if (rid!=0){
            resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            return context.getResources().getDimensionPixelSize(resourceId);
        }else
            return 0;
    }
}
