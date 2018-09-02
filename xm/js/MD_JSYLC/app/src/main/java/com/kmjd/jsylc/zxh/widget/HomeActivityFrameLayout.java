package com.kmjd.jsylc.zxh.widget;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.kmjd.jsylc.zxh.MainApplication;
import com.kmjd.jsylc.zxh.R;
import com.kmjd.jsylc.zxh.utils.DpPxUtil;
import com.kmjd.jsylc.zxh.utils.MobileHeightUtil;
import com.kmjd.jsylc.zxh.utils.SPUtils;
import com.kmjd.jsylc.zxh.utils.TAG;

/**
 * Created by androidshuai on 2017/12/27.
 */

public class HomeActivityFrameLayout extends FrameLayout{
    public HomeActivityFrameLayout(@NonNull Context context) {
        this(context, null);
    }

    public HomeActivityFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HomeActivityFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLocation(context);
    }

    private int mHomeMenuBottomLeftX;//主页面菜单控件底部左边的X位置
    private int mHomeMenuBottomLeftY;//主页面菜单控件底部左边的Y位置
    private int mHomeHYZXTopLeftX;//主页面会员中心按钮头部左边X位置
    private int mHomeHYZXTopLeftY;//主页面会员中心按钮头部坐标Y位置

    /**
     *计算主页面头部菜单(75*(根据机型判断获取高度))和会员中心按钮的位置 ((根据机型判断获取高度) * 屏幕 * 1/5)
     */
    private void initLocation(Context context) {
        //获取主页面菜单控件底部左边的X位置 = 屏幕的宽度 - 75dp的px;
        mHomeMenuBottomLeftX = MobileHeightUtil.getScreenWidth(context)
                - DpPxUtil.getPxByDp(75);
        //获取主页面菜单控件底部左边的Y位置 = 状态栏的高度 + (根据机型判断获取高度)dp的px
        mHomeMenuBottomLeftY = MobileHeightUtil.getStatusHeight(context)
                + DpPxUtil.getPxByDp(SPUtils.getInt(MainApplication.applicationContext, SPUtils.SP_KEY.TOOLBAR_HEIGHT.toString()));
        //获取主页面会员中心按钮头部左边X位置 = 4 * 屏幕的宽度 / 5
        mHomeHYZXTopLeftX = 4 * MobileHeightUtil.getScreenWidth(context)/5;
        //获取主页面会员中心按钮头部坐标Y位置 = 屏幕的高度 - 虚拟按键的高度 - (根据机型判断获取高度)dp的px
        mHomeHYZXTopLeftY = MobileHeightUtil.getScreenHeight(context)
                - DpPxUtil.getPxByDp(SPUtils.getInt(MainApplication.applicationContext, SPUtils.SP_KEY.RADIOBUTTOM_HEIGHT.toString()));
        SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.IS_APPEAR.toString(), false);
    }

    private LinearLayout tou = null;
    private FrameLayout web_rg_parent = null;
    int statusBarHeight = 0;

    /**
     *webFragment里面的弹窗在什么情况下会消失：
         1.点web的相应的位置（通过js回调callAndroidByJs改变SP里的值）
         2.按返回键（onKeydown如果SP里的值为true就改为false）
         3.直接关掉程序(HomeActivityFrameLayout里的initLocation方法里设置sp里的值为fasle)
         4.重定向直接跳转到下一页面(shouldOverrideUrlLoading方法里设置sp里的值为fasle)
         5.被挤下线(登出方法HomeActivity里的logoutSuccessful里设置sp里的值为fasle)
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (null == tou | null == web_rg_parent ){
            tou = this.findViewById(R.id.tou);
            web_rg_parent = this.findViewById(R.id.web_rg_parent);
        }
        if (statusBarHeight <=0 ){
            statusBarHeight = MobileHeightUtil.getStatusBarHeight_1((Activity) getContext());
        }
        //因为getTop()和getBottom()是相对于父布局的，而MotionEvent的getRawY()是相对于屏幕的，所以要算上状态栏的高度
        if (ev.getRawY()<(tou.getBottom()+statusBarHeight)||ev.getRawY()>(web_rg_parent.getTop()+statusBarHeight)){
            return SPUtils.getBoolean(MainApplication.applicationContext, SPUtils.SP_KEY.IS_APPEAR.toString(), false);
        }else {
            return super.onInterceptTouchEvent(ev);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                int evRawX = (int) ev.getRawX();
                int evRawY = (int) ev.getRawY();
                //处理主页面头部菜单
                if (evRawX > mHomeMenuBottomLeftX && evRawY < mHomeMenuBottomLeftY){
                }else {
                    //事件分发在了主页面头部菜单的外面
                    SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.HOME_MENU_IS_SHOWING.toString(), false);
                }

                //处理主页面会员中心菜单
                if (evRawX > mHomeHYZXTopLeftX && evRawY > mHomeHYZXTopLeftY){
                }else {
                    //事件分发在了主页面会员中心的外面
                    SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.HOME_HYZX_MENU_IS_SHOWING.toString(), false);
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
