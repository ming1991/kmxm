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
 * Created by androidshuai on 2018/2/5.
 */

public class LoginWebFragmentRelativeLayout extends RelativeLayout {

    public LoginWebFragmentRelativeLayout(Context context) {
        this(context, null);
    }

    public LoginWebFragmentRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoginWebFragmentRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLocation(context);
    }

    private int mTitleBottomLocation = 0;

    private void initLocation(Context context) {
        //标题栏底部的位置 = 状态栏的高度 + 标题栏自身的高度
        mTitleBottomLocation = MobileHeightUtil.getStatusHeight(context)
                + DpPxUtil.getPxByDp(SPUtils.getInt(MainApplication.applicationContext, SPUtils.SP_KEY.TOOLBAR_HEIGHT.toString()));
        SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.LOGINWEBFRAGMENT_WEB_DIALOG.toString(), false);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                int evRawY = (int) ev.getRawY();
                if (evRawY < mTitleBottomLocation){
                    return SPUtils.getBoolean(MainApplication.applicationContext, SPUtils.SP_KEY.LOGINWEBFRAGMENT_WEB_DIALOG.toString(), false);
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }
}
