package com.kmjd.jsylc.zxh.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.kmjd.jsylc.zxh.MainApplication;
import com.kmjd.jsylc.zxh.utils.DpPxUtil;
import com.kmjd.jsylc.zxh.utils.MobileHeightUtil;
import com.kmjd.jsylc.zxh.utils.SPUtils;

/**
 * Created by androidshuai on 2017/12/27.
 */

public class LoginFragmentRelativeLayout extends RelativeLayout {
    public LoginFragmentRelativeLayout(Context context) {
        this(context, null);
    }

    public LoginFragmentRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoginFragmentRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLocation(context);
    }

    private int menuX1 = 0;
    private int menuX2 = 0;
    private int menuY = 0;

    private void initLocation(Context context) {
        //获取菜单键左边X的位置 = 20dp的px + 4*(屏幕宽度值 - 40dp的px)/5
        menuX1 = DpPxUtil.getPxByDp(20) + 4 * (MobileHeightUtil.getScreenWidth(context) - DpPxUtil.getPxByDp(40))/5;
        //获取菜单键右边X的位置 = 屏幕宽度值 - 20dp的px;
        menuX2 = MobileHeightUtil.getScreenWidth(context) - DpPxUtil.getPxByDp(20);
        //获取菜单键Y的位置 = 屏幕的高度 - 虚拟的高度 -62dp的px
        menuY = MobileHeightUtil.getScreenHeight(context) - DpPxUtil.getPxByDp(62);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (ev.getRawX() > menuX1 && ev.getRawX() < menuX2 && ev.getRawY() > menuY){

                }else {
                    //事件分发在了登录页菜单键的外面
                    SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.LOGIN_MENU_IS_SHOWING.toString(), false);
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
