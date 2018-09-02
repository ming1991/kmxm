package com.md.jsyxzs_cn.zym_xs.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;


/**
 * Created by zym on 2016/11/12.
 */

public class StatusBarManager {
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void setStatusBarColor(Activity activity, int color){
        //首先设置状态栏透明
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //生成一个状态栏大小的View
        View statusBarView = createStatusBarView(activity,color);
        //再获取到跟布局
        ViewGroup decorView = (ViewGroup)activity.getWindow().getDecorView();
        //在当前窗口跟布局的起始位置添加创建的statusBarView；
        decorView.addView(statusBarView);
        ViewGroup rootView = (ViewGroup)activity.findViewById(
                android.R.id.content);
        //设置当前根布局的背景色跟状态栏颜色一致
        //rootView.setBackgroundColor(Color.parseColor(color));
        //获取到跟布局的我们添加进去的statusBarView
        ViewGroup hasBeenAddedStatusBarView = (ViewGroup)rootView.getChildAt(0);
        //此时自己创建的状态栏大小的statusBarView被添加进了跟布局，并且在状态栏的正下方
        //那我们就要让我们的statusBarView“替换掉”系统状态栏，也就是让我们的statusBarView
        //适应系统窗口（此时为状态栏）
        hasBeenAddedStatusBarView.setFitsSystemWindows(true);
        //不允许在状态栏区域绘制其他View视图(比如不让ListView的item内容在此显示)
        hasBeenAddedStatusBarView.setClipToPadding(true);



    }

    private static View createStatusBarView(Activity activity , int color){
        ;
        //首先获取状态栏的高度
        int statusBarHeight = getStatusBarHeight(activity);
        //动态创建状态栏大小的View
        View statusBarView = new View(activity);
        //设置View的大小
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,statusBarHeight);
        statusBarView.setLayoutParams(layoutParams);
        statusBarView.setBackgroundColor(color);
        //返回View视图
        return statusBarView;
    }

    private static int getStatusBarHeight(Activity activity){
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return activity.getResources().getDimensionPixelSize(resourceId);
    }
}
